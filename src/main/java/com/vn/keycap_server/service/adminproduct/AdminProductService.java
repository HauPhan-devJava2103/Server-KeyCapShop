package com.vn.keycap_server.service.adminproduct;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vn.keycap_server.dto.request.product.AdminListProductRequest;
import com.vn.keycap_server.dto.request.product.AdminProductSpecificationRequest;
import com.vn.keycap_server.dto.request.product.AdminProductVariantRequest;
import com.vn.keycap_server.dto.request.product.CreateAdminProductRequest;
import com.vn.keycap_server.dto.response.product.AdminProductDetailResponse;
import com.vn.keycap_server.dto.response.product.AdminProductItemResponse;
import com.vn.keycap_server.dto.response.product.ProductOptionResponse;
import com.vn.keycap_server.dto.response.product.ProductSpecificationResponse;
import com.vn.keycap_server.dto.response.product.ProductVariantResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.AdminProductMapper;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.modal.ProductImage;
import com.vn.keycap_server.modal.ProductSpecification;
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.modal.ProductVariantAttribute;
import com.vn.keycap_server.modal.Brand;
import com.vn.keycap_server.modal.Category;
import com.vn.keycap_server.modal.Media;
import com.vn.keycap_server.modal.ProductType;
import com.vn.keycap_server.repository.BrandRepository;
import com.vn.keycap_server.repository.CartItemRepository;
import com.vn.keycap_server.repository.CategoryRepository;
import com.vn.keycap_server.repository.MediaRepository;
import com.vn.keycap_server.repository.OrderItemRepository;
import com.vn.keycap_server.repository.ProductImageRepository;
import com.vn.keycap_server.repository.ProductRepository;
import com.vn.keycap_server.repository.ProductSpecificationRepository;
import com.vn.keycap_server.repository.ProductTypeRepository;
import com.vn.keycap_server.repository.ProductVariantRepository;
import com.vn.keycap_server.repository.ReviewRepository;
import com.vn.keycap_server.repository.WishlistRepository;
import com.vn.keycap_server.repository.projection.ProductRatingSummaryProjection;
import com.vn.keycap_server.repository.projection.ProductVariantSummaryProjection;
import com.vn.keycap_server.utils.EMediaStatus;
import com.vn.keycap_server.utils.EProductStatus;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ sản phẩm dành cho khu vực admin.
 * Luồng list admin ưu tiên phân trang đúng, tránh N+1 và chỉ trả các trường FE
 * cần.
 */
@Service
@RequiredArgsConstructor
public class AdminProductService implements IAdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTypeRepository productTypeRepository;
    private final BrandRepository brandRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final MediaRepository mediaRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final WishlistRepository wishlistRepository;
    private final AdminProductMapper adminProductMapper;

    /**
     * Lấy danh sách sản phẩm quản trị theo page, limit và search.
     *
     * @param request query request từ FE admin
     * @return Page chứa danh sách AdminProductItemResponse
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AdminProductItemResponse> getProducts(AdminListProductRequest request) {
        // 1. Chuẩn hóa request và tạo Pageable theo thứ tự sản phẩm mới cập nhật trước
        String search = normalizeSearch(request.getSearch());
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getLimit(),
                Sort.by(Sort.Direction.DESC, "updatedAt").and(Sort.by(Sort.Direction.DESC, "id")));

        // 2. Query trang sản phẩm, chỉ fetch các quan hệ ManyToOne để phân trang không
        // bị sai
        Page<Product> productPage = productRepository.findAdminProducts(search, pageable);
        List<Product> products = productPage.getContent();

        if (products.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, productPage.getTotalElements());
        }

        // 3. Lấy danh sách ID sản phẩm trong trang hiện tại để batch query dữ liệu phụ
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        // 4. Batch load ảnh đại diện, thống kê biến thể và rating để tránh N+1 query
        Map<Long, String> imageByProductId = getDisplayImageByProductId(productIds);
        Map<Long, ProductVariantSummaryProjection> variantSummaryByProductId = getVariantSummaryByProductId(productIds);
        Map<Long, Double> ratingByProductId = getRatingByProductId(productIds);

        // 5. Map entity sang DTO và gán các trường tính toán theo contract FE admin
        List<AdminProductItemResponse> responses = products.stream()
                .map(product -> buildAdminProductItem(
                        product,
                        imageByProductId,
                        variantSummaryByProductId,
                        ratingByProductId))
                .toList();

        // 6. Trả Page DTO để controller đóng gói pagination metadata
        return new PageImpl<>(responses, pageable, productPage.getTotalElements());
    }

    /**
     * Lấy chi tiết sản phẩm theo ID choadmin.
     *
     * @param productId ID sản phẩm cần lấy chi tiết
     * @return chi tiết sản phẩm theo contract FE admin
     */
    @Override
    @Transactional(readOnly = true)
    public AdminProductDetailResponse getProductById(Long productId) {
        // 1. Validate ID đầu vào để tránh query dữ liệu không hợp lệ
        validateProductId(productId);

        // 2. Lấy product kèm category/type/brand; các danh sách con được query riêng để
        // rõ luồng
        Product product = productRepository.findAdminProductDetailById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

        // 3. Batch load các dữ liệu con cần cho form admin
        List<ProductImage> images = productImageRepository.findDisplayImagesByProductId(productId);
        List<ProductVariant> variants = productVariantRepository.findByProductIdWithAttributes(productId);
        List<ProductSpecification> specifications = productSpecificationRepository
                .findByProductIdOrderBySortOrderAscIdAsc(productId);

        // 4. Map các field cơ bản từ product sang DTO
        AdminProductDetailResponse response = adminProductMapper.toAdminProductDetailResponse(product);

        // 5. Gán dữ liệu ảnh, biến thể, option, thông số và các field tính toán
        enrichDetailResponse(response, productId, images, variants, specifications);

        return response;
    }

    /**
     * Tạo sản phẩm mới.
     *
     * @param request payload tạo sản phẩm từ FE admin
     * @return chi tiết sản phẩm vừa tạo
     */
    @Override
    @Transactional
    public AdminProductDetailResponse createProduct(CreateAdminProductRequest request) {
        // 1. Chuẩn hóa và kiểm tra slug để đảm bảo URL sản phẩm không bị trùng
        String slug = normalizeSlug(request.getSlug(), request.getName());
        validateSlugAvailable(slug);

        // 2. Lấy và kiểm tra các phân loại bắt buộc của sản phẩm
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục sản phẩm"));
        ProductType type = productTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại sản phẩm"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu"));

        // 3. Kiểm tra và kích hoạt media theo URL ảnh FE gửi lên
        List<String> imageUrls = collectImageUrls(request);
        activateMediasByUrls(imageUrls);

        // 4. Chuẩn hóa SKU và kiểm tra trùng SKU trước khi tạo entity
        List<String> skus = buildAndValidateSkus(slug, request.getVariants());

        // 5. Tạo Product aggregate root cùng các collection con
        Product product = Product.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .status(EProductStatus.AVAILABLE)
                .category(category)
                .type(type)
                .brand(brand)
                .build();

        product.setImages(buildProductImages(product, request.getImageUrl(), request.getThumbnailUrl()));
        product.setSpecifications(buildProductSpecifications(product, request.getSpecifications()));
        product.setVariants(buildProductVariants(product, request.getVariants(), skus));

        // 6. Lưu product, cascade sẽ lưu images, specifications, variants và attributes
        Product savedProduct = productRepository.save(product);

        // 7. Trả chi tiết sản phẩm vừa tạo theo đúng contract FE admin
        return getProductById(savedProduct.getId());
    }

    /**
     * Cập nhật sản phẩm hiện có theo ID.
     *
     * @param productId ID sản phẩm cần cập nhật
     * @param request   payload cập nhật từ FE admin
     * @return chi tiết sản phẩm sau cập nhật
     */
    @Override
    @Transactional
    public AdminProductDetailResponse updateProduct(Long productId, CreateAdminProductRequest request) {
        // 1. Validate ID và load product hiện tại
        validateProductId(productId);
        Product product = productRepository.findAdminProductDetailById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

        // 2. Chuẩn hóa slug và đảm bảo slug không bị sản phẩm khác sử dụng
        String slug = normalizeSlug(request.getSlug(), request.getName());
        validateSlugAvailableForUpdate(slug, productId);

        // 3. Lấy và kiểm tra các phân loại bắt buộc của sản phẩm
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục sản phẩm"));
        ProductType type = productTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại sản phẩm"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu"));

        // 4. Kích hoạt media theo URL ảnh mới FE gửi lên
        List<String> imageUrls = collectImageUrls(request);
        activateMediasByUrls(imageUrls);

        // 5. Load các collection hiện tại để sync có kiểm soát
        List<ProductVariant> currentVariants = productVariantRepository.findByProductIdWithAttributes(productId);

        // 6. Chuẩn hóa SKU và kiểm tra trùng, cho phép SKU thuộc chính variant hiện tại
        List<String> skus = buildAndValidateSkusForUpdate(slug, request.getVariants(), currentVariants);

        // 7. Cập nhật field cơ bản của product
        product.setName(request.getName().trim());
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setType(type);
        product.setBrand(brand);
        product.setStatus(EProductStatus.AVAILABLE);

        // 8. Sync các collection con theo payload mới
        syncProductImages(product, request.getImageUrl(), request.getThumbnailUrl());
        syncProductSpecifications(product, request.getSpecifications());
        syncProductVariants(product, currentVariants, request.getVariants(), skus);

        // 9. Lưu product, orphanRemoval/cascade xử lý các collection con
        Product savedProduct = productRepository.save(product);

        // 10. Trả chi tiết sản phẩm sau cập nhật
        return getProductById(savedProduct.getId());
    }

    /**
     * Xóa sản phẩm nếu chưa có đơn hàng, hoặc chuyển sang không bán nữa nếu đã có đơn hàng.
     *
     * @param productId ID sản phẩm cần xóa hoặc ngừng bán
     */
    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        // 1. Validate ID dành riêng cho luồng delete
        validateDeleteProductId(productId);

        // 2. Load product cần xóa, không dùng lại luồng detail để tránh kéo dữ liệu không cần thiết
        Product product = getProductForDelete(productId);

        // 3. Dọn dữ liệu tạm của khách hàng để sản phẩm không còn trong giỏ hàng/yêu thích
        removeProductFromUserTemporaryData(productId);

        // 4. Nếu sản phẩm đã có đơn hàng thì chỉ chuyển sang không bán nữa để giữ lịch sử mua hàng
        if (orderItemRepository.existsByVariantProductId(productId)) {
            markProductAsUnavailableForDelete(product);
            return;
        }

        // 5. Nếu chưa phát sinh đơn hàng thì hard delete, cascade/orphanRemoval sẽ xóa dữ liệu con
        productRepository.delete(product);
    }

    /**
     * Validate ID sản phẩm cho riêng API delete admin.
     */
    private void validateDeleteProductId(Long productId) {
        // 1. ID phải tồn tại và là số dương
        if (productId == null || productId <= 0) {
            throw new BadRequestException("ID sản phẩm không hợp lệ");
        }
    }

    /**
     * Lấy product phục vụ delete, chỉ cần entity gốc để hard delete hoặc cập nhật trạng thái.
     */
    private Product getProductForDelete(Long productId) {
        // 1. Nếu không tìm thấy thì trả 404 theo chuẩn API hiện tại
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));
    }

    /**
     * Dọn dữ liệu tạm đang tham chiếu đến sản phẩm trước khi xóa hoặc ngừng bán.
     */
    private void removeProductFromUserTemporaryData(Long productId) {
        // 1. Xóa khỏi wishlist để sản phẩm ngừng bán không còn nằm trong danh sách yêu thích
        wishlistRepository.deleteByProductId(productId);

        // 2. Xóa khỏi cart để người dùng không checkout nhầm sản phẩm đã bị admin xóa/ngừng bán
        cartItemRepository.deleteByVariantProductId(productId);
    }

    /**
     * Chuyển sản phẩm sang trạng thái không bán nữa khi không thể hard delete vì đã có đơn hàng.
     */
    private void markProductAsUnavailableForDelete(Product product) {
        // 1. Cập nhật trạng thái trên entity gốc, không thay đổi thông tin khác của sản phẩm
        product.setStatus(EProductStatus.UNAVAILABLE);

        // 2. Lưu lại để public API/cart/order không cho bán tiếp sản phẩm này
        productRepository.save(product);
    }

    /**
     * Chuẩn hóa từ khóa tìm kiếm để repository có thể bỏ qua khi chuỗi rỗng.
     */
    private String normalizeSearch(String search) {
        // 1. Nếu search rỗng hoặc chỉ chứa khoảng trắng thì coi như không tìm kiếm
        if (!StringUtils.hasText(search)) {
            return null;
        }

        // 2. Trim để tránh khoảng trắng đầu/cuối làm lệch kết quả
        return search.trim();
    }

    /**
     * Validate ID sản phẩm cho API chi tiết admin.
     */
    private void validateProductId(Long productId) {
        // 1. ID phải tồn tại và là số dương
        if (productId == null || productId <= 0) {
            throw new BadRequestException("ID sản phẩm không hợp lệ");
        }
    }

    /**
     * Chuẩn hóa slug từ request hoặc sinh từ tên sản phẩm nếu FE không gửi.
     */
    private String normalizeSlug(String requestSlug, String productName) {
        // 1. Ưu tiên slug FE gửi, nếu rỗng thì sinh từ tên sản phẩm
        String source = StringUtils.hasText(requestSlug) ? requestSlug : productName;
        String normalized = removeVietnameseAccent(source)
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        // 2. Slug sau chuẩn hóa phải còn giá trị
        if (!StringUtils.hasText(normalized)) {
            throw new BadRequestException("Slug sản phẩm không hợp lệ");
        }

        return normalized;
    }

    /**
     * Loại bỏ dấu tiếng Việt để sinh slug/SKU ổn định.
     */
    private String removeVietnameseAccent(String value) {
        // 1. Tách dấu Unicode và loại bỏ phần mark
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // 2. Xử lý riêng chữ đ/Đ vì không nằm trong nhóm mark
        return normalized.replace("đ", "d").replace("Đ", "D");
    }

    /**
     * Kiểm tra slug có thể sử dụng cho sản phẩm mới hay không.
     */
    private void validateSlugAvailable(String slug) {
        // 1. Slug là unique trên bảng products
        if (productRepository.existsBySlug(slug)) {
            throw new BadRequestException("Slug sản phẩm đã tồn tại");
        }
    }

    /**
     * Kiểm tra slug khi cập nhật, bỏ qua chính sản phẩm hiện tại.
     */
    private void validateSlugAvailableForUpdate(String slug, Long productId) {
        // 1. Slug được phép trùng với chính product hiện tại, nhưng không được trùng product khác
        if (productRepository.existsBySlugAndIdNot(slug, productId)) {
            throw new BadRequestException("Slug sản phẩm đã tồn tại");
        }
    }

    /**
     * Gom tất cả URL ảnh sản phẩm từ request.
     */
    private List<String> collectImageUrls(CreateAdminProductRequest request) {
        // 1. Gom ảnh chính và gallery, loại bỏ rỗng và trùng lặp
        Set<String> urls = new LinkedHashSet<>();
        if (StringUtils.hasText(request.getImageUrl())) {
            urls.add(request.getImageUrl().trim());
        }
        if (request.getThumbnailUrl() != null) {
            request.getThumbnailUrl().stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .forEach(urls::add);
        }

        return new ArrayList<>(urls);
    }

    /**
     * Chuyển trạng thái media từ PENDING sang ACTIVE dựa trên secureUrl FE gửi.
     */
    private void activateMediasByUrls(List<String> imageUrls) {
        // 1. Không có ảnh thì request đã bị validation chặn, đoạn này chỉ bảo vệ thêm
        if (imageUrls.isEmpty()) {
            throw new BadRequestException("Sản phẩm phải có ít nhất một ảnh");
        }

        // 2. Tìm media trong DB theo secureUrl vì FE chỉ gửi URL ảnh
        List<Media> medias = mediaRepository.findAllBySecureUrlIn(imageUrls);
        Map<String, Media> mediaByUrl = medias.stream()
                .collect(Collectors.toMap(
                        Media::getSecureUrl,
                        media -> media,
                        (first, ignored) -> first));

        // 3. Nếu có URL không tồn tại trong bảng medias thì không cho lưu để tránh ảnh
        // rác ngoài hệ thống
        List<String> missingUrls = imageUrls.stream()
                .filter(url -> !mediaByUrl.containsKey(url))
                .toList();
        if (!missingUrls.isEmpty()) {
            throw new BadRequestException("Một hoặc nhiều ảnh chưa được lưu trong hệ thống media");
        }

        // 4. Đánh dấu các media đã thực sự được dùng bởi sản phẩm là ACTIVE
        medias.forEach(media -> media.setStatus(EMediaStatus.ACTIVE));
        mediaRepository.saveAll(medias);
    }

    /**
     * Chuẩn hóa danh sách SKU và kiểm tra trùng trong request lẫn database.
     */
    private List<String> buildAndValidateSkus(String productSlug, List<AdminProductVariantRequest> variants) {
        // 1. Sinh/chuẩn hóa SKU cho từng variant
        List<String> skus = new ArrayList<>();
        for (int index = 0; index < variants.size(); index++) {
            AdminProductVariantRequest variant = variants.get(index);
            String sku = StringUtils.hasText(variant.getSku())
                    ? normalizeSku(variant.getSku())
                    : generateSku(productSlug, variant.getAttributes(), index);
            skus.add(sku);
        }

        // 2. Kiểm tra trùng SKU ngay trong request
        if (new HashSet<>(skus).size() != skus.size()) {
            throw new BadRequestException("Danh sách biến thể chứa SKU bị trùng");
        }

        // 3. Kiểm tra trùng SKU với database
        if (productVariantRepository.existsBySkuIn(skus)) {
            throw new BadRequestException("Một hoặc nhiều SKU đã tồn tại trong hệ thống");
        }

        return skus;
    }

    /**
     * Chuẩn hóa và kiểm tra SKU khi cập nhật sản phẩm.
     */
    private List<String> buildAndValidateSkusForUpdate(
            String productSlug,
            List<AdminProductVariantRequest> requestVariants,
            List<ProductVariant> currentVariants) {
        // 1. Sinh/chuẩn hóa SKU tương tự create
        List<String> skus = new ArrayList<>();
        for (int index = 0; index < requestVariants.size(); index++) {
            AdminProductVariantRequest variant = requestVariants.get(index);
            String sku = StringUtils.hasText(variant.getSku())
                    ? normalizeSku(variant.getSku())
                    : generateSku(productSlug, variant.getAttributes(), index);
            skus.add(sku);
        }

        // 2. Kiểm tra trùng SKU trong chính request
        if (new HashSet<>(skus).size() != skus.size()) {
            throw new BadRequestException("Danh sách biến thể chứa SKU bị trùng");
        }

        // 3. Validate variantId trong request phải thuộc sản phẩm hiện tại
        Map<Long, ProductVariant> currentVariantById = currentVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, variant -> variant));
        for (AdminProductVariantRequest requestVariant : requestVariants) {
            Long variantId = requestVariant.getId();
            if (variantId != null && variantId > 0 && !currentVariantById.containsKey(variantId)) {
                throw new BadRequestException("Biến thể cập nhật không thuộc sản phẩm hiện tại");
            }
        }

        // 4. Kiểm tra SKU trùng database, cho phép trùng với chính variant đang được cập nhật
        Map<String, Long> requestVariantIdBySku = new LinkedHashMap<>();
        for (int index = 0; index < requestVariants.size(); index++) {
            Long variantId = requestVariants.get(index).getId();
            if (variantId != null && variantId > 0) {
                requestVariantIdBySku.put(skus.get(index), variantId);
            }
        }

        for (ProductVariant existingVariant : productVariantRepository.findBySkuIn(skus)) {
            Long allowedVariantId = requestVariantIdBySku.get(existingVariant.getSku());
            if (!Objects.equals(existingVariant.getId(), allowedVariantId)) {
                throw new BadRequestException("Một hoặc nhiều SKU đã tồn tại trong hệ thống");
            }
        }

        return skus;
    }

    /**
     * Chuẩn hóa SKU FE gửi lên.
     */
    private String normalizeSku(String sku) {
        // 1. SKU thống nhất dạng uppercase, bỏ khoảng trắng dư
        String normalized = sku.trim().toUpperCase();
        if (!StringUtils.hasText(normalized)) {
            throw new BadRequestException("SKU biến thể không hợp lệ");
        }
        return normalized;
    }

    /**
     * Sinh SKU từ slug sản phẩm và attributes khi FE không gửi SKU.
     */
    private String generateSku(String productSlug, Map<String, String> attributes, int index) {
        // 1. Ghép product slug với các giá trị attribute đã chuẩn hóa
        String attributePart = attributes == null ? ""
                : attributes.values().stream()
                        .filter(StringUtils::hasText)
                        .map(this::removeVietnameseAccent)
                        .map(value -> value.toUpperCase().replaceAll("[^A-Z0-9]+", "-").replaceAll("(^-|-$)", ""))
                        .filter(StringUtils::hasText)
                        .collect(Collectors.joining("-"));

        // 2. Nếu variant không có attribute rõ ràng thì dùng số thứ tự để tránh SKU
        // rỗng
        String base = productSlug.toUpperCase().replace("-", "_");
        return StringUtils.hasText(attributePart)
                ? base + "-" + attributePart
                : base + "-" + (index + 1);
    }

    /**
     * Tạo danh sách ảnh sản phẩm từ request.
     */
    private List<ProductImage> buildProductImages(Product product, String imageUrl, List<String> thumbnailUrls) {
        // 1. Ảnh chính luôn được lưu là primary và sortOrder = 0
        List<ProductImage> images = new ArrayList<>();
        images.add(ProductImage.builder()
                .product(product)
                .url(imageUrl.trim())
                .primary(true)
                .sortOrder(0)
                .build());

        // 2. Gallery được lưu sau ảnh chính, bỏ qua URL trùng ảnh chính
        if (thumbnailUrls != null) {
            int sortOrder = 1;
            Set<String> usedUrls = new LinkedHashSet<>();
            usedUrls.add(imageUrl.trim());

            for (String thumbnailUrl : thumbnailUrls) {
                if (!StringUtils.hasText(thumbnailUrl)) {
                    continue;
                }
                String url = thumbnailUrl.trim();
                if (!usedUrls.add(url)) {
                    continue;
                }
                images.add(ProductImage.builder()
                        .product(product)
                        .url(url)
                        .primary(false)
                        .sortOrder(sortOrder++)
                        .build());
            }
        }

        return images;
    }

    /**
     * Tạo danh sách thông số kỹ thuật từ request.
     */
    private List<ProductSpecification> buildProductSpecifications(
            Product product,
            List<AdminProductSpecificationRequest> specifications) {
        // 1. Nếu FE không gửi specifications thì lưu danh sách rỗng
        if (specifications == null || specifications.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Chuyển từng specification sang entity và gán sortOrder theo thứ tự FE gửi
        List<ProductSpecification> result = new ArrayList<>();
        for (int index = 0; index < specifications.size(); index++) {
            AdminProductSpecificationRequest specification = specifications.get(index);
            result.add(ProductSpecification.builder()
                    .product(product)
                    .name(specification.getName().trim())
                    .value(specification.getValue().trim())
                    .sortOrder(index)
                    .build());
        }
        return result;
    }

    /**
     * Tạo danh sách biến thể sản phẩm từ request.
     */
    private List<ProductVariant> buildProductVariants(
            Product product,
            List<AdminProductVariantRequest> variants,
            List<String> skus) {
        // 1. Map từng variant request sang entity và tạo attributes con
        List<ProductVariant> result = new ArrayList<>();
        for (int index = 0; index < variants.size(); index++) {
            AdminProductVariantRequest request = variants.get(index);
            ProductVariant variant = ProductVariant.builder()
                    .product(product)
                    .sku(skus.get(index))
                    .price(request.getPrice())
                    .originalPrice(request.getOriginalPrice())
                    .percentDiscount(request.getPercentDiscount() == null ? 0 : request.getPercentDiscount())
                    .stockQuantity(request.getStockQuantity())
                    .build();

            variant.setAttributes(buildVariantAttributes(variant, request.getAttributes()));
            result.add(variant);
        }
        return result;
    }

    /**
     * Tạo danh sách thuộc tính cho một biến thể.
     */
    private List<ProductVariantAttribute> buildVariantAttributes(
            ProductVariant variant,
            Map<String, String> attributes) {
        // 1. Chuyển map attributes từ FE sang danh sách entity con
        return attributes.entrySet().stream()
                .map(entry -> ProductVariantAttribute.builder()
                        .variant(variant)
                        .name(entry.getKey().trim())
                        .value(entry.getValue().trim())
                        .build())
                .toList();
    }

    /**
     * Sync danh sách ảnh sản phẩm khi cập nhật.
     */
    private void syncProductImages(
            Product product,
            String imageUrl,
            List<String> thumbnailUrls) {
        // 1. Khởi tạo collection lazy để Hibernate quản lý orphanRemoval trên collection gốc
        product.getImages().size();

        // 2. Xóa collection cũ và thêm collection mới theo payload
        product.getImages().clear();
        product.getImages().addAll(buildProductImages(product, imageUrl, thumbnailUrls));
    }

    /**
     * Sync danh sách thông số kỹ thuật khi cập nhật.
     */
    private void syncProductSpecifications(
            Product product,
            List<AdminProductSpecificationRequest> specifications) {
        // 1. Khởi tạo collection lazy để Hibernate quản lý orphanRemoval trên collection gốc
        product.getSpecifications().size();

        // 2. Xóa collection cũ và thêm collection mới theo payload
        product.getSpecifications().clear();
        product.getSpecifications().addAll(buildProductSpecifications(product, specifications));
    }

    /**
     * Sync danh sách biến thể khi cập nhật sản phẩm.
     */
    private void syncProductVariants(
            Product product,
            List<ProductVariant> currentVariants,
            List<AdminProductVariantRequest> requestVariants,
            List<String> skus) {
        // 1. Khởi tạo collection lazy để Hibernate quản lý orphanRemoval/cascade trên collection gốc
        product.getVariants().size();

        Map<Long, ProductVariant> currentVariantById = currentVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, variant -> variant));
        Set<Long> requestedExistingIds = requestVariants.stream()
                .map(AdminProductVariantRequest::getId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        // 2. Xóa các variant cũ không còn trong payload
        product.getVariants().removeIf(variant -> variant.getId() != null
                && !requestedExistingIds.contains(variant.getId()));

        // 3. Cập nhật variant cũ hoặc thêm variant mới
        for (int index = 0; index < requestVariants.size(); index++) {
            AdminProductVariantRequest requestVariant = requestVariants.get(index);
            ProductVariant variant = resolveVariantForSync(product, currentVariantById, requestVariant);

            variant.setProduct(product);
            variant.setSku(skus.get(index));
            variant.setPrice(requestVariant.getPrice());
            variant.setOriginalPrice(requestVariant.getOriginalPrice());
            variant.setPercentDiscount(requestVariant.getPercentDiscount() == null ? 0 : requestVariant.getPercentDiscount());
            variant.setStockQuantity(requestVariant.getStockQuantity());
            syncVariantAttributes(variant, requestVariant.getAttributes());

            if (variant.getId() == null || variant.getId() <= 0) {
                product.getVariants().add(variant);
            }
        }
    }

    /**
     * Lấy variant hiện tại để update hoặc tạo mới nếu request không có ID.
     */
    private ProductVariant resolveVariantForSync(
            Product product,
            Map<Long, ProductVariant> currentVariantById,
            AdminProductVariantRequest requestVariant) {
        // 1. Variant có ID thì cập nhật entity cũ
        Long variantId = requestVariant.getId();
        if (variantId != null && variantId > 0) {
            return currentVariantById.get(variantId);
        }

        // 2. Variant không có ID là variant mới
        return ProductVariant.builder()
                .product(product)
                .build();
    }

    /**
     * Sync attributes của một variant.
     */
    private void syncVariantAttributes(ProductVariant variant, Map<String, String> attributes) {
        // 1. Với variant cũ, clear collection hiện tại để orphanRemoval xóa attribute cũ
        if (variant.getAttributes() == null) {
            variant.setAttributes(new ArrayList<>());
        } else {
            variant.getAttributes().clear();
        }

        // 2. Thêm lại attributes theo payload mới
        variant.getAttributes().addAll(buildVariantAttributes(variant, attributes));
    }

    /**
     * Lấy ảnh hiển thị theo productId.
     */
    private Map<Long, String> getDisplayImageByProductId(List<Long> productIds) {
        // 1. Repository đã sắp xếp primary trước, service giữ ảnh đầu tiên cho mỗi sản
        // phẩm
        return productImageRepository.findDisplayImagesByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getProduct().getId(),
                        ProductImage::getUrl,
                        (first, ignored) -> first,
                        LinkedHashMap::new));
    }

    /**
     * Lấy thống kê giá và tồn kho theo productId.
     */
    private Map<Long, ProductVariantSummaryProjection> getVariantSummaryByProductId(List<Long> productIds) {
        // 1. Batch query minPrice và totalStockQuantity theo từng sản phẩm
        return productVariantRepository.findVariantSummariesByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductVariantSummaryProjection::getProductId,
                        summary -> summary));
    }

    /**
     * Lấy rating trung bình theo productId.
     */
    private Map<Long, Double> getRatingByProductId(List<Long> productIds) {
        // 1. Batch query rating trung bình, không gọi từng product riêng lẻ
        return reviewRepository.findRatingSummariesByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductRatingSummaryProjection::getProductId,
                        ProductRatingSummaryProjection::getRating));
    }

    /**
     * Build response cho một sản phẩm admin từ entity và các dữ liệu batch đã gom.
     */
    private AdminProductItemResponse buildAdminProductItem(
            Product product,
            Map<Long, String> imageByProductId,
            Map<Long, ProductVariantSummaryProjection> variantSummaryByProductId,
            Map<Long, Double> ratingByProductId) {
        // 1. Map các field cơ bản và các object category/type/brand bằng MapStruct
        AdminProductItemResponse response = adminProductMapper.toAdminProductItemResponse(product);

        // 2. Gán ảnh đại diện đã batch load
        response.setImageUrl(imageByProductId.get(product.getId()));

        // 3. Gán giá thấp nhất và tổng tồn kho từ thống kê variants
        ProductVariantSummaryProjection variantSummary = variantSummaryByProductId.get(product.getId());
        response.setMinPrice(variantSummary == null ? BigDecimal.ZERO : variantSummary.getMinPrice());
        response.setTotalStockQuantity(variantSummary == null ? 0 : variantSummary.getTotalStockQuantity().intValue());

        // 4. Gán rating trung bình, mặc định 0 nếu sản phẩm chưa có đánh giá
        response.setRating(ratingByProductId.getOrDefault(product.getId(), 0D));

        return response;
    }

    /**
     * Gán các dữ liệu con và field tính toán cho response chi tiết sản phẩm.
     */
    private void enrichDetailResponse(
            AdminProductDetailResponse response,
            Long productId,
            List<ProductImage> images,
            List<ProductVariant> variants,
            List<ProductSpecification> specifications) {
        // 1. Gán ảnh chính và danh sách thumbnail theo thứ tự hiển thị
        response.setImageUrl(getMainImageUrl(images));
        response.setThumbnailUrl(images.stream()
                .map(ProductImage::getUrl)
                .toList());

        // 2. Map variants và options phục vụ bảng biến thể của FE admin
        response.setVariants(mapVariantResponses(variants));
        response.setOptions(buildOptions(variants));

        // 3. Tính khoảng giá và tổng tồn kho từ toàn bộ variants
        response.setMinPrice(getMinPrice(variants));
        response.setMaxPrice(getMaxPrice(variants));
        response.setTotalStockQuantity(getTotalStockQuantity(variants));

        // 4. Map thông số kỹ thuật theo đúng contract FE
        response.setSpecifications(mapSpecificationResponses(specifications));

        // 5. Gán rating trung bình, mặc định 0 nếu chưa có đánh giá
        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        response.setRating(avgRating == null ? 0D : avgRating);
    }

    /**
     * Lấy ảnh chính từ danh sách ảnh đã được repository sắp xếp ưu tiên.
     */
    private String getMainImageUrl(List<ProductImage> images) {
        // 1. Danh sách đã sắp xếp primary trước nên lấy phần tử đầu tiên
        return images.isEmpty() ? null : images.get(0).getUrl();
    }

    /**
     * Map danh sách variant entity sang response DTO.
     */
    private List<ProductVariantResponse> mapVariantResponses(List<ProductVariant> variants) {
        // 1. Duyệt từng variant và chuyển attributes sang Map<String, String>
        return variants.stream()
                .map(variant -> ProductVariantResponse.builder()
                        .id(variant.getId())
                        .sku(variant.getSku())
                        .attributes(mapAttributes(variant.getAttributes()))
                        .price(variant.getPrice())
                        .originalPrice(variant.getOriginalPrice())
                        .percentDiscount(variant.getPercentDiscount() == null
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(variant.getPercentDiscount()))
                        .stockQuantity(variant.getStockQuantity() == null ? 0 : variant.getStockQuantity())
                        .build())
                .toList();
    }

    /**
     * Chuyển danh sách attributes của variant sang map để FE dễ dựng bảng biến thể.
     */
    private Map<String, String> mapAttributes(List<ProductVariantAttribute> attributes) {
        // 1. Nếu variant chưa có attribute thì trả map rỗng để FE không bị null
        if (attributes == null || attributes.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. Nếu dữ liệu cũ có trùng tên attribute, giữ giá trị đầu tiên
        return attributes.stream()
                .sorted(Comparator.comparing(ProductVariantAttribute::getId))
                .collect(Collectors.toMap(
                        ProductVariantAttribute::getName,
                        ProductVariantAttribute::getValue,
                        (first, ignored) -> first,
                        LinkedHashMap::new));
    }

    /**
     * Gom options từ toàn bộ attributes của variants.
     */
    private List<ProductOptionResponse> buildOptions(List<ProductVariant> variants) {
        // 1. Gom các giá trị attribute theo tên option, giữ thứ tự xuất hiện để FE hiển
        // thị ổn định
        Map<String, Set<String>> optionValues = new LinkedHashMap<>();

        for (ProductVariant variant : variants) {
            if (variant.getAttributes() == null) {
                continue;
            }

            for (ProductVariantAttribute attribute : variant.getAttributes()) {
                optionValues
                        .computeIfAbsent(attribute.getName(), ignored -> new LinkedHashSet<>())
                        .add(attribute.getValue());
            }
        }

        // 2. Chuyển map option sang DTO ProductOptionResponse
        return optionValues.entrySet().stream()
                .map(entry -> ProductOptionResponse.builder()
                        .name(entry.getKey())
                        .values(entry.getValue().toArray(new String[0]))
                        .build())
                .toList();
    }

    /**
     * Tính giá thấp nhất trong danh sách variants.
     */
    private BigDecimal getMinPrice(List<ProductVariant> variants) {
        // 1. Lấy min price, nếu không có variant thì trả về 0
        return variants.stream()
                .map(ProductVariant::getPrice)
                .filter(price -> price != null)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Tính giá cao nhất trong danh sách variants.
     */
    private BigDecimal getMaxPrice(List<ProductVariant> variants) {
        // 1. Lấy max price, nếu không có variant thì trả về 0
        return variants.stream()
                .map(ProductVariant::getPrice)
                .filter(price -> price != null)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Tính tổng tồn kho trong danh sách variants.
     */
    private Integer getTotalStockQuantity(List<ProductVariant> variants) {
        // 1. Cộng tồn kho của tất cả variants, null được tính là 0
        return variants.stream()
                .map(ProductVariant::getStockQuantity)
                .filter(quantity -> quantity != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Map danh sách specification entity sang response DTO.
     */
    private List<ProductSpecificationResponse> mapSpecificationResponses(List<ProductSpecification> specifications) {
        // 1. Chỉ trả name/value vì FE admin hiện chỉ dùng hai trường này
        return specifications.stream()
                .map(specification -> ProductSpecificationResponse.builder()
                        .name(specification.getName())
                        .value(specification.getValue())
                        .build())
                .toList();
    }
}
