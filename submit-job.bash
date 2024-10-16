#!/bin/bash

# Default values
DEFAULT_MAIN_CLASS="example.OlympicGamesApp"
DEFAULT_MASTER_IP="172.28.1.1"
DEFAULT_JAR_FILE="mysparkproject_2.12-1.7.2.jar"

# Check if -e or --edit is specified
if [[ "$1" == "-e" || "$1" == "--edit" ]]; then
    # Ask for the class name containing the main method
    read -p "Enter the class name containing the main method (default is $DEFAULT_MAIN_CLASS): " main_class
    main_class=${main_class:-$DEFAULT_MAIN_CLASS}

    # Confirm the master node IP
    read -p "Enter the master node IP (default is $DEFAULT_MASTER_IP): " master_ip
    master_ip=${master_ip:-$DEFAULT_MASTER_IP}

    # Ask for the JAR file name
    read -p "Enter the JAR file name (default is $DEFAULT_JAR_FILE): " jar_file
    jar_file=${jar_file:-$DEFAULT_JAR_FILE}
else
    # Use default values
    main_class=$DEFAULT_MAIN_CLASS
    master_ip=$DEFAULT_MASTER_IP
    jar_file=$DEFAULT_JAR_FILE
fi

# Define the path to the Spark submit command
SPARK_SUBMIT="/opt/spark/bin/spark-submit"

# Define the packages required
PACKAGES="com.datastax.spark:spark-cassandra-connector_2.12:3.2.0,com.github.jnr:jnr-posix:3.1.15"

# Define the master node URL
MASTER_URL="spark://$master_ip:7077"

# Execute the Spark submit command and filter out lines containing "info"
$SPARK_SUBMIT --packages $PACKAGES --master $MASTER_URL --class $main_class $jar_file 2>&1 | grep -v -i "info" | grep -v "info:" | grep -v -i "WARN" | grep -v -i "found"
