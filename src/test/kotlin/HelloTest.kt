import app.routes
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method
import org.junit.Test

class HelloTest {

    @Test
    fun routeTest(){
        val response = routes.invoke(org.http4k.core.Request(Method.GET, "/hello?name=bob"))
        assertThat(response.bodyString(), equalTo("Hello, bob!"))
    }
}