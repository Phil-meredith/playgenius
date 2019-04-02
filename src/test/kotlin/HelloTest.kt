import app.Routes.routes
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method
import org.junit.Test
import org.http4k.core.Request
import org.http4k.core.Status

class HelloTest {

    @Test
    fun routeTest(){
        val response = routes.invoke(Request(Method.GET, "/hello?name=bob"))
        assertThat(response.bodyString(), equalTo("Hello, bob!"))
    }
}