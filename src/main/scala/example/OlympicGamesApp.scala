package example

import org.apache.spark.sql.SparkSession;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.functions.{
  min,
  max,
  countDistinct,
  row_number,
  desc
};
import org.apache.spark.sql.expressions.Window;

object OlympicGamesApp {
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
      .load()
    println(
      "Exo 1 ------------------------------------------------------------:"
    );
    df.show()

    // Call functions and display results
    println(
      "Exo 2 ------------------------------------------------------------:"
    );
    println("1- Gold Medals:");
    val goldMedalsDF = getGoldMedals(df)
    goldMedalsDF.show()
    println("2-Gold Medalists 2016:");
    val goldMedalists2016DF = getGoldMedalists2016(df)
    goldMedalists2016DF.show()
    println("3-French Gold Medalists 2016:");
    val frenchGoldMedalists2016DF = getFrenchGoldMedalists2016(df)
    frenchGoldMedalists2016DF.show()
    println("4-Time Interval:");
    val timeIntervalDF = getTimeInterval(df)
    timeIntervalDF.show()
    println("5-Winter 2002 Events:");
    val winter2002EventsDF = getWinter2002Events(df)
    winter2002EventsDF.show()
    println("6-Winter 2002 Event Count:");
    val winter2002EventCountDF = getWinter2002EventCount(df)
    winter2002EventCountDF.show()
    println("7-Most Events Sport Per JO:");
    val mostEventsSportPerJO = getMostEventsSportPerJO(df)
    mostEventsSportPerJO.show()

    mySession.close()
  }

  def getGoldMedals(df: DataFrame): DataFrame = {
    df.filter("medal = 'Gold'")
  }

  def getGoldMedalists2016(df: DataFrame): DataFrame = {
    df.filter("medal = 'Gold' AND year = 2016")
      .select("name", "athlete_id", "noc")
      .distinct()
      .orderBy("name")
  }

  def getFrenchGoldMedalists2016(df: DataFrame): DataFrame = {
    df.filter("medal = 'Gold' AND year = 2016 AND noc = 'FRA'")
      .select("name", "athlete_id", "noc")
      .distinct()
      .orderBy("name")
  }

  def getTimeInterval(df: DataFrame): DataFrame = {
    df.agg(min("year").as("Earliest Year"), max("year").as("Latest Year"))
  }

  def getWinter2002Events(df: DataFrame): DataFrame = {
    df.filter("year = 2002 AND season = 'Winter'")
      .select("event", "sport")
      .distinct()
  }

  def getWinter2002EventCount(df: DataFrame): DataFrame = {
    df.filter("year = 2002 AND season = 'Winter'")
      .groupBy("sport")
      .agg(countDistinct("event").as("Event Count"))
  }

  def getMostEventsSportPerJO(df: DataFrame): DataFrame = {
    df.groupBy("year", "season", "sport")
      .agg(countDistinct("event").as("Event Count"))
      .withColumn(
        "rank",
        row_number().over(
          Window.partitionBy("year", "season").orderBy(desc("Event Count"))
        )
      )
      .filter("rank = 1")
      .select("year", "season", "sport", "Event Count")
  }
}
