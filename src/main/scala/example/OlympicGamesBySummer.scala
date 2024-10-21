package example

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.cassandra._;
import org.apache.spark.sql.functions.{desc, when, col, sum};

object OlympicGamesBySummer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf(true)
      .set("spark.cassandra.auth.username", "kebraoui")
      .set("spark.cassandra.auth.password", "cichorium");

    val mySession = SparkSession
      .builder()
      .master("spark://172.28.1.3:7077")
      .config(conf)
      .appName("mySparkProject")
      .getOrCreate();

    val df = mySession.read
      .cassandraFormat("athlete_events_by_games", "donnees_jo")
      .load();
    val dfRegions = mySession.read
      .option("delimiter", ",")
      .option("header", "true")
      .csv("file:///home/caron/noc_regions.csv")
      .select("NOC", "region", "notes");
    val dfJoined = df.join(dfRegions, df("NOC") === dfRegions("NOC"), "left");
    val dfSummer = dfJoined.filter("season = 'Summer'")
    val dfWinter = dfJoined.filter("season = 'Winter'")

    println(
      "Exo 3 ------------------------------------------------------------:"
    );
    // println(
    //   "-------------------------------le nombre de pays participants ETE--------------------------------"
    // )
    // val summerParticipationDF = getSummerParticipationPerCountry(dfSummer)
    // summerParticipationDF.show()

    println(
      "-------------------------------le pourcentage de participation par genre par pays ETE--------------------------------"
    )
    val genderPercentageDF = getGenderPercentage(dfSummer)
    genderPercentageDF.show()

  }

  def getSummerParticipationPerCountry(df: DataFrame): DataFrame = {
    df.groupBy("region").count().orderBy(desc("count"))
  }
//   def getGenderPercentage(df: DataFrame): DataFrame = {
//     // participation total par pays
//     val countParPay =
//       df.groupBy("region").count().withColumnRenamed("count", "countPay")

//     // participation par genre et par pays
//     val countParGenre = df
//       .groupBy("region", "sex")
//       .count()
//       .withColumnRenamed("count", "countGenre")

//     // pourcentage de participation par genre par pays
//     val percentageDF = countParGenre
//       .join(countParPay, "region")
//       .withColumn(
//         "percentage",
//         (countParGenre("countGenre") / countParPay("countPay")) * 100
//       )
//       .select("region", "sex", "percentage")

//     percentageDF
//   }
  // ------------------------------------------------------------
  def getGenderPercentage(df: DataFrame): DataFrame = {
    // participation total par pays
    val countParPay =
      df.groupBy("region").count().withColumnRenamed("count", "countPay")

    // participation par genre et par pays
    val countParGenre = df
      .groupBy("region", "sex")
      .count()
      .withColumnRenamed("count", "countGenre")

    // pourcentage de participation par genre par pays
    val percentageDF = countParGenre
      .join(countParPay, "region")
      .withColumn("percentage", (col("countGenre") / col("countPay")) * 100)
      .select("region", "sex", "percentage")

    percentageDF
  }
}
