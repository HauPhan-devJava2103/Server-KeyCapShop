# Báo cáo Table Relationship

Tổng số Entity: 20
Tổng số bảng: 20
Tổng số Relationship: 44

Trong đó:
Association: 0
Directed Association: 13
Aggregation: 2
Composition: 11
Generalization: 18
Dependency: 0
Realization: 0
Khác: 0

| STT | Bảng A | Bảng B | Annotation JPA | Loại UML | Ký hiệu | Chiều ký hiệu | Cardinality | Owning Side | Foreign Key | Giải thích |
| --- | ------ | ------ | -------------- | -------- | ------- | ------------- | ----------- | ----------- | ----------- | ---------- |
| 1 | `users` | `addresses` | `@OneToMany(mappedBy="user")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng users | 1 —— * | `addresses` | `addresses.user_id -> users.id` | Một User sở hữu nhiều Address, khi xóa User sẽ tự động xóa Address (CascadeType.ALL, orphanRemoval=true). |
| 2 | `brands` | `products` | `@OneToMany(mappedBy="brand")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng brands | 1 —— * | `products` | `products.brand_id -> brands.id` | Một Brand có nhiều Product, cấu hình JPA sử dụng CascadeType.ALL và orphanRemoval=true. |
| 3 | `categories` | `products` | `@OneToMany(mappedBy="category")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng categories | 1 —— * | `products` | `products.category_id -> categories.id` | Một Category phân loại nhiều Product, có CascadeType.ALL và orphanRemoval=true. |
| 4 | `product_types` | `products` | `@OneToMany(mappedBy="type")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng product_types | 1 —— * | `products` | `products.type_id -> product_types.id` | Một Product Type chứa nhiều Product, có CascadeType.ALL và orphanRemoval=true. |
| 5 | `users` | `invalidated_tokens` | `@OneToMany(mappedBy="user")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng users | 1 —— * | `invalidated_tokens` | `invalidated_tokens.user_id -> users.id` | Một User có nhiều Token bị hủy, khi xóa User sẽ xóa token (OnDelete.CASCADE). |
| 6 | `products` | `product_variants` | `@OneToMany(mappedBy="product")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng products | 1 —— * | `product_variants` | `product_variants.product_id -> products.id` | Một sản phẩm có nhiều biến thể. Xóa sản phẩm sẽ xóa hết biến thể. |
| 7 | `products` | `product_images` | `@OneToMany(mappedBy="product")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng products | 1 —— * | `product_images` | `product_images.product_id -> products.id` | Sản phẩm có nhiều ảnh. Xóa sản phẩm sẽ xóa ảnh. |
| 8 | `products` | `product_specifications` | `@OneToMany(mappedBy="product")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng products | 1 —— * | `product_specifications` | `product_specifications.product_id -> products.id` | Sản phẩm có nhiều thông số. Xóa sản phẩm sẽ xóa thông số. |
| 9 | `product_variants` | `product_variant_attributes` | `@OneToMany(mappedBy="variant")`, `@ManyToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng product_variants | 1 —— * | `product_variant_attributes` | `product_variant_attributes.variant_id -> product_variants.id` | Biến thể có các thuộc tính. Xóa biến thể sẽ xóa thuộc tính. |
| 10 | `reviews` | `review_replies` | `@OneToOne(mappedBy="review")`, `@OneToOne` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng reviews | 1 —— 0..1 | `review_replies` | `review_replies.review_id -> reviews.id` | Mỗi đánh giá có thể có 1 phản hồi. Xóa đánh giá sẽ xóa phản hồi. |
| 11 | `reviews` | `review_images` | `@ElementCollection`, `@CollectionTable` | Composition | Hình thoi đặc | Hình thoi nằm phía bảng reviews | 1 —— * | `review_images` | `review_images.review_id -> reviews.id` | Bảng phụ lưu link ảnh cho review. Tồn tại phụ thuộc vào review. |
| 12 | `users` | `medias` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng medias | * —— 0..1 | `users` | `users.avatar_media_id -> medias.id` | Một người dùng có thể trỏ tới một media làm ảnh đại diện. |
| 13 | `medias` | `users` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng users | * —— 1 | `medias` | `medias.uploaded_by -> users.id` | Một media được upload bởi một người dùng. |
| 14 | `cart_items` | `users` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng users | * —— 1 | `cart_items` | `cart_items.user_id -> users.id` | Item trong giỏ hàng thuộc về một người dùng. |
| 15 | `cart_items` | `product_variants` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng product_variants | * —— 1 | `cart_items` | `cart_items.variant_id -> product_variants.id` | Item giỏ hàng tham chiếu biến thể sản phẩm. |
| 16 | `orders` | `users` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng users | * —— 1 | `orders` | `orders.user_id -> users.id` | Đơn hàng được tạo bởi một người dùng. |
| 17 | `orders` | `addresses` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng addresses | * —— 0..1 | `orders` | `orders.address_id -> addresses.id` | Đơn hàng gửi tới một địa chỉ giao hàng. |
| 18 | `order_items` | `product_variants` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng product_variants | * —— 1 | `order_items` | `order_items.variant_id -> product_variants.id` | Chi tiết đơn hàng mua một biến thể sản phẩm. |
| 19 | `wishlists` | `users` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng users | * —— 1 | `wishlists` | `wishlists.user_id -> users.id` | Sản phẩm yêu thích thuộc về một người dùng. |
| 20 | `wishlists` | `products` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng products | * —— 1 | `wishlists` | `wishlists.product_id -> products.id` | Bản ghi yêu thích lưu một sản phẩm. |
| 21 | `reviews` | `orders` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng orders | * —— 0..1 | `reviews` | `reviews.order_id -> orders.id` | Đánh giá có thể liên kết với đơn hàng đã mua. |
| 22 | `reviews` | `products` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng products | * —— 1 | `reviews` | `reviews.product_id -> products.id` | Đánh giá ghi nhận cho một sản phẩm. |
| 23 | `reviews` | `users` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng users | * —— 1 | `reviews` | `reviews.user_id -> users.id` | Đánh giá được viết bởi người dùng. |
| 24 | `review_replies` | `users` | `@ManyToOne` | Directed Association | Mũi tên | Mũi tên trỏ về bảng users | * —— 1 | `review_replies` | `review_replies.user_id -> users.id` | Phản hồi đánh giá được viết bởi admin/staff. |
| 25 | `orders` | `order_items` | `@OneToMany(mappedBy="order")`, `@ManyToOne` | Aggregation | Hình thoi rỗng | Hình thoi nằm phía bảng orders | 1 —— * | `order_items` | `order_items.order_id -> orders.id` | Đơn hàng gộp nhiều chi tiết (không thiết lập cascade trong JPA). |
| 26 | `orders` | `order_status_history` | `@OneToMany(mappedBy="order")`, `@ManyToOne` | Aggregation | Hình thoi rỗng | Hình thoi nằm phía bảng orders | 1 —— * | `order_status_history` | `order_status_history.order_id -> orders.id` | Đơn hàng lưu lại lịch sử thay đổi trạng thái (không có cascade). |
| 27 | `addresses` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) để dùng chung id, createdAt, updatedAt. |
| 28 | `brands` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 29 | `cart_items` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 30 | `categories` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 31 | `medias` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 32 | `orders` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 33 | `order_items` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 34 | `products` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 35 | `product_images` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 36 | `product_specifications` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 37 | `product_types` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 38 | `product_variants` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 39 | `product_variant_attributes` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 40 | `reviews` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 41 | `review_replies` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 42 | `users` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 43 | `invalidated_tokens` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
| 44 | `wishlists` | `abstract_entity` | `extends AbstractEntity` | Generalization | Tam giác rỗng | Tam giác hướng về abstract_entity | 1 —— 1 | N/A | N/A | Kế thừa BaseEntity (@MappedSuperclass) |
