package dasturlash.homework4

import jakarta.annotation.Generated
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.criteria.CriteriaBuilder
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.Date

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP)var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP)var lastModifiedDate: Date? = null,
    @CreatedBy var createdBy: String? = null,
    @LastModifiedBy var lastModifiedBy: String? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
    )

@Entity
@Table(name = "users")
class User(
    var username: String? = null,
    var fullname: String? = null,
    var email: String? = null,
    var password: String? = null,
    var address: String? = null,
    @Enumerated(EnumType.STRING) var role: UserRole? = null,
    @Enumerated(EnumType.STRING) var status: UserStatus? = null,
): BaseEntity()

@Entity
@Table(name = "orders")
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @Enumerated(EnumType.STRING) var status: OrderStatus,
    var totalAmount: BigDecimal,
) : BaseEntity()

@Entity
class Payment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order?=null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User?=null,
    @Enumerated(EnumType.STRING) var paymentMethod: PaymentMethod,
    var amount: BigDecimal?=null,
): BaseEntity()

@Entity
class Category(
    var name: String?=null,
    var description: String?=null,
) : BaseEntity()

@Entity
class Product(
    var name: String? = null,
    var description: String? = null,
    var price: BigDecimal? = null,
    var stockCount: Int? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var category: Category? = null,
) : BaseEntity()

@Entity
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order?=null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product?=null,
    var quantity: Int?=null,
    var unitPrice: BigDecimal?=null,
    var totalAmount: BigDecimal?=null,
) : BaseEntity()