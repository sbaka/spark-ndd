package example

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.cassandra._;
import org.apache.spark.sql.functions.{
  desc,
  when,
  col,
  sum,
  avg,
  countDistinct
};
import spire.syntax.group
import org.apache.hadoop.shaded.org.checkerframework.checker.units.qual.s

object OlympicGamesBySummer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf(true)
      .set("spark.cassandra.auth.username", "kebraoui")
      .set("spark.cassandra.auth.password", "cichorium");

    val mySession = SparkSession
      .builder()
      .master("spark://172.28.1.1:7077")
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
    val dfJoined = df.join(dfRegions, Seq("NOC"), "left")
    val dfSummer = dfJoined.filter("season = 'Summer'")
    val dfWinter = dfJoined.filter("season = 'Winter'")

    println(
      "Exo 3 ------------------------------------------------------------:"
    )

    println(
      "-------------------------------le nombre de pays participants ETE--------------------------------"
    )
    val summerParticipationDF = getSummerParticipationPerCountry(dfSummer)
    summerParticipationDF.show()

    println(
      "-------------------------------le pourcentage de participation par genre par pays ETE--------------------------------"
    )
    val genderPercentageDF = getGenderPercentage(dfSummer)
    genderPercentageDF.show()

    println(
      "-------------------------------Age Moyenn ETE--------------------------------"
    )
    val ageMoyenDF = getAgeMoyen(dfSummer)
    ageMoyenDF.show()

    println(
      "-------------------------------Age Moyenn HIVER--------------------------------"
    )
    val ageMoyenDFHiver = getAgeMoyen(dfWinter)
    ageMoyenDFHiver.show()

    println(
      "-------------------------------le nombre de médailles par pays ETE (pondéré)--------------------------------"
    )
    val nbOfMedalsDF = getNbOfMedalsPerCountry(dfSummer)
    nbOfMedalsDF.show()

    println(
      "-------------------------------le nombre de médailles par pays ETE (trié par priorité)--------------------------------"
    )
    val medalsSortedByPriorityDF = getMedalsSortedByPriority(dfSummer)
    medalsSortedByPriorityDF.show()

    println("-------------------------------END-------------------------------")

    mySession.close()

  }

  def getSummerParticipationPerCountry(df: DataFrame): DataFrame = {
    df.groupBy("games")
      .agg(
        countDistinct("NOC").as("nb_pays")
      )
  }

  // ------------------------------------------------------------
  def getGenderPercentage(df: DataFrame): DataFrame = {
    // participation total par pays
    df
      .groupBy("NOC", "region")
      .agg(
        sum(when(col("sex") === "F", 1).otherwise(0)).as("nb_femmes"),
        sum(when(col("sex") === "M", 1).otherwise(0)).as("nb_hommes")
      )
      .withColumn(
        "pctage_femmes",
        (col("nb_femmes") / (col("nb_femmes") + col("nb_hommes")) * 100)
      )
      .withColumn(
        "pctage_hommes",
        (col("nb_hommes") / (col("nb_femmes") + col("nb_hommes")) * 100)
      )
      .withColumn(
        "ratio_nbfemmes_pour_cent_hommes",
        col("pctage_femmes") / col("pctage_hommes") * 100
      )
      .orderBy("NOC")
  }

  // ------------------------------------------------------------
  def getAgeMoyen(df: DataFrame): DataFrame = {
    df.groupBy("NOC", "region")
      .agg(
        avg("age").as("age_moyen"),
        avg(when(col("sex") === "M", col("age"))).as("age_moyen_homme"),
        avg(when(col("sex") === "F", col("age"))).as("age_moyen_femme")
      )
      .orderBy("NOC")
  }

  // ------------------------------------------------------------
  def getNbOfMedalsPerCountry(df: DataFrame): DataFrame = {
    df.filter("year = 2016")
      .groupBy("region")
      .agg(
        sum(when(col("medal") === "Gold", 1).otherwise(0)).as("nb_gold"),
        sum(when(col("medal") === "Silver", 1).otherwise(0)).as("nb_silver"),
        sum(when(col("medal") === "Bronze", 1).otherwise(0)).as("nb_bronze")
      )
      .withColumn(
        "total",
        col("nb_gold") * 3 + col("nb_silver") * 2 + col("nb_bronze")
      )
      .orderBy(desc("total"))
      .limit(10)
  }

  def getMedalsSortedByPriority(df: DataFrame): DataFrame = {
    df.filter("year = 2016")
      .groupBy("region")
      .agg(
        sum(when(col("medal") === "Gold", 1).otherwise(0)).as("nb_gold"),
        sum(when(col("medal") === "Silver", 1).otherwise(0)).as("nb_silver"),
        sum(when(col("medal") === "Bronze", 1).otherwise(0)).as("nb_bronze")
      )
      .orderBy(desc("nb_gold"), desc("nb_silver"), desc("nb_bronze"))
      .limit(10)
  }
}
