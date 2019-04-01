import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class HelloTest {

    @Test
    fun test(){
        assertThat(1, equalTo(1))
    }
}