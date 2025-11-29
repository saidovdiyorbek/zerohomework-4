package dasturlash.homework4

import jakarta.annotation.Generated
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.criteria.CriteriaBuilder
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.util.Date

@MappedSuperclass
@Entity
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP)var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP)var lastModifiedDate: Date? = null,
    @CreatedBy var createdBy: String? = null,
    @LastModifiedBy var lastModifiedBy: String? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
    )

@Entity
class User(
    var username: String,
    var fullname: String,
    var email: String,
    var address: String,
    var role: UserRole
): BaseEntity()

@Entity
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    var status: OrderStatus,
    var totalAmount: BigDecimal,
) : BaseEntity()

@Entity
class Payment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    var paymentMethod: PaymentMethod,
    var amount: BigDecimal,
): BaseEntity()

@Entity
class Category(
    var name: String,
    var description: String,
) : BaseEntity()

@Entity
class Product(
    var name: String,
    var description: String,
    var price: BigDecimal,
    var stockCount: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var category: Category,
) : BaseEntity()

@Entity
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,
    var quantity: Int,
    var unitPrice: BigDecimal,
    var totalAmount: BigDecimal,
) : BaseEntity()