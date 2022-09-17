import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openwilma.kotlin.enums.UserType

class UserTypeTest {
    @Test
    fun checkUserTypeParsers() {
        assertEquals(UserType.WILMA_ACCOUNT, UserType.fromString("passwd"))
        assertEquals(UserType.WILMA_ACCOUNT, UserType.fromInt(7))
    }
}