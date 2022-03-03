# Docker container for acmeair-driver
This is a quick instruction to build and run dockerized version of acmeair-driver.

## Building a container image
Running maven in the top directory builds a driver's container image, too:
```shell
cd acmeair-quarkus
mvn package
```

Running maven in this directory also works if you want to build only the driver image.

## Running the driver as a container
Make sure Acme Air services are running.

In a terminal, run following command. The JMeter's output is shown in the terminal.
```shell
docker run acmeair-driver
```

Some parameters can be passed to the script via environment variables by using `--env name=value` option:
```shell
docker run --env HOST=myserver.on.a.cloud --env DURATION=600 --env THREADS=32 acmeair-driver
```

Available parameters are:
|Name    |Description                   |Default value|
|:------:|:-----------------------------|:-----------:|
|HOST    |Host name of the ningx service| localhost |
|PORT    |HTTP port of the ningx service| 80 |
|THREADS |Number of driver threads      |  1 |
|DURATION|Seconds to drive Acme Air     | 60 |
|SUMMARY_INTERVAL|Interval (sec) to report summarizer statistics| 10 |

## Saving the summarizer log
To save the JMeter summarizer log, redirect the output of the container to a file, or copy the log file in the container by using `docker cp` command:
```shell
docker cp `docker ps -aqf ancestor=acmeair-driver -n=1`:/deployments/acmeair-driver.log .
```
