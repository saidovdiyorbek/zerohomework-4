package dasturlash.homework4

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): Page<T>

}

class BaseRepositoryImpl<T : BaseEntity>(
    entiInformation: JpaEntityInformation<T, Long>, entityManager: EntityManager
): SimpleJpaRepository<T, Long>(entiInformation, entityManager), BaseRepository<T> {
    override fun findByIdAndDeletedFalse(id: Long): T? = findByIdOrNull(id)?.run { if (deleted) null else this }

    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run{
        deleted = true
        save(this)
    }

    override fun trashList(ids: List<Long>): List<T?> {
        TODO("Not yet implemented")
    }

    override fun findAllNotDeleted(): List<T> {
        TODO("Not yet implemented")
    }

    override fun findAllNotDeleted(pageable: Pageable): Page<T> {
        TODO("Not yet implemented")
    }
}

@Repository
interface UserRepository : BaseRepository<User> {
    fun existsByUsername(username: String): Boolean

    /*@Modifying
    @Transactional
    @Query("update User u set u.balance = u.balance - ?2" +
            " where u.balance >= ?2 and u.id = ?1")
    fun deductBalance(userId: Long?, amount: BigDecimal)*/

    /*@Query("select u from User u where u.id = ?1 and u.balance >= ?2")
    fun checkBalance(userId: Long?, amount: BigDecimal): User?*/

    @Query("select u from User u where u.username = ?1 and u.deleted = false" )
    fun findByUsername(username: String?): User?
}
//User repo

//Category repo
@Repository
interface CategoryRepository : BaseRepository<Category> {
    fun findByName(name: String): Category?

}

@Repository
interface ProductRepository : BaseRepository<Product>{

    @Modifying
    @Transactional
    @Query("update Product p set p.stockCount = p.stockCount - ?2 where p.id = ?1 and p.stockCount >= ?2")
    fun deductCount(productId: Long?, count: Long)
}

@Repository
interface OrderRepository : BaseRepository<Order> {

    @Query("""
        select o 
        from Order o 
        join User u on o.user.id = u.id
        where u.deleted = false 
    """)
    fun findAllByUserId(userId: Long): List<Order>

    @Query("""
        select distinct 
        o.user.username as username,
        o.totalAmount as totalAmount,
        o.status as status
        from Order o
        join User u on o.user.id = u.id
        where o.user.id = ?1 and o.id = ?2
    """)
    fun findByUserIdAndIdProjection(userId: Long, orderId: Long): OrderInfoResponseProjection?

    fun findByUserIdAndId(userId: Long, orderId: Long): Order?

    @Query("""
        select count(o.totalAmount) as counts,
        sum(o.totalAmount) as totalAmountMonthly
        from Order o
        join User u on o.user.id = u.id
        where o.user.id = ?1 and EXTRACT(month from o.createdDate) = ?2
    """)
    fun findUserMonthlyOrdered(userId: Long, month: Byte): OrderCountMonthlyProjection?
}

@Repository
interface OrderItemRepository : BaseRepository<OrderItem>{

    fun findAllByOrderId(orderId: Long): List<OrderItem>
}

@Repository
interface PaymentRepository : BaseRepository<Payment> {

    fun findByUserIdAndDeletedFalse(userId: Long): List<Payment>?
}