import app.Routes
import app.StatsGenerator
import clients.FileReadingsClient
import clients.Reading
import clients.ReadingsClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method
import org.junit.Test
import org.http4k.core.Request
import org.http4k.format.Jackson.asJsonObject


val noOpReadings = object:ReadingsClient{
    override fun getReadings(fileName: String): Sequence<Reading> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class RoutesTest {

    @Test
    fun helloTest(){
        val response = Routes(StatsGenerator((noOpReadings))).routes.invoke(Request(Method.GET, "/hello?name=bob"))
        assertThat(response.bodyString(), equalTo("Hello, bob!"))
    }

    @Test
    fun averagePositionTest(){
        val response = Routes(StatsGenerator(FileReadingsClient { name -> this.javaClass.getResourceAsStream("$name.csv")})).routes.invoke(Request(Method.GET, "/averagePosition/testReadings"))
        assertThat(response.bodyString().asJsonObject(), equalTo("""{
        |"0" : { "9A26": {
        |"first" : 1.2593092105263148,
        |"second" : 0.8262828947368425,
        |"third" : 0.648848684210526} }}""".trimMargin().asJsonObject()))
    }

    @Test
    fun totalDistanceTest(){
        val response = Routes(StatsGenerator(FileReadingsClient { name -> this.javaClass.getResourceAsStream("$name.csv")})).routes.invoke(Request(Method.GET, "/totalDistance/testReadings"))
        assertThat(response.bodyString().asJsonObject(), equalTo("""{
            |"9A26":65.63461690915123
            |}""".trimMargin().asJsonObject()))
    }
}