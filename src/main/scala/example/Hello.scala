package example
import org.apache.spark.sql.cassandra._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Hello {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf(true)
      .set("spark.cassandra.auth.username", "kebraoui")
      .set("spark.cassandra.auth.password", "cichorium")

    val mySession = SparkSession
      .builder()
      .master("spark://172.28.1.3:7077")
      .config(conf)
      .appName("mySparkProject")
      .getOrCreate();

    val df =
      mySession.read
        .cassandraFormat("athlete_events_by_games", "donnees_jo")
        .load()

    df.show()

    mySession.close()
  }
}
