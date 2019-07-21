import app.Routes
import app.StatsGenerator
import clients.FileReadingsClient
import clients.MatchClient
import clients.Reading
import clients.ReadingsClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method
import org.junit.Test
import org.http4k.core.Request
import org.http4k.format.Jackson.asJsonObject
import org.http4k.template.TemplateRenderer



class RoutesTest {
    private val readingsClient = FileReadingsClient({ name -> this.javaClass.getResourceAsStream("$name.csv") }, { null })

    @Test
    fun averagePositionTest(){
        val response = Routes(
            StatsGenerator(readingsClient), MatchClient()).routes.invoke(Request(Method.GET, "/averagePosition/testReadings"))
        assertThat(response.bodyString().asJsonObject(), equalTo("""{
        |"0" : { "9A26": {
        |"first" : 1.2593092105263148,
        |"second" : 0.8262828947368425,
        |"third" : 0.648848684210526} }}""".trimMargin().asJsonObject()))
    }

    @Test
    fun totalDistanceTest(){
        val response = Routes(StatsGenerator(readingsClient), MatchClient()).routes.invoke(Request(Method.GET, "/totalDistance/testReadings"))
        assertThat(response.bodyString().asJsonObject(), equalTo("""{
            |"9A26":20.10633371670572
            |}""".trimMargin().asJsonObject()))
    }

    @Test
    fun cumulativeDistanceTest(){
        val response = Routes(StatsGenerator(readingsClient), MatchClient()).routes.invoke(Request(Method.GET, "/cumulativeDistance/testReadings"))
        val actual = response.bodyString().asJsonObject()
        assertThat(actual.get("9A26")[0].toString(), equalTo("""{"x":"2019-03-03T23:16:00Z","y":16.170022204912822}"""))
    }

    @Test
    fun maximumSpeedTest(){
        val response = Routes(StatsGenerator(readingsClient), MatchClient()).routes.invoke(Request(Method.GET, "/maximumSpeed/testReadings"))
        val actual = response.bodyString().asJsonObject()
        assertThat(actual.get("9A26").toString(), equalTo("1.636457412991491"))
    }
}