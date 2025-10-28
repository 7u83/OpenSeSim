# OpenSeSim

OpenSeSim - A Stock Exchange Simulator

OpenSeSim is a stock market simulation software that does not use historical 
prices. Instead, prices are formed through the supply and demand of the 
trading bots.

---
## How to compile
To compile and run OpenSeSim, you need **Java** and **Apache Maven**. OpenSeSim 
requires **JDK 11** or newer, as defined in the `pom.xml`.

    sudo dnf install java-11-openjdk-devel maven

When java and maven are installed clone the project and compile:

    git clone https://github.com/7u83/OpenSeSim.git
    cd OpenSeSim
    mvn package -Prelease

There is now OpenSeSim-dist.jar in ./target.

## How to run
After building, run OpenSeSim with:

    java -jar target/OpenSeSim-dist.jar

