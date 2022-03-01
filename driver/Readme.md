# Docker container for acmeair-driver
This is a quick instruction to build and run dockerized version of acmeair-driver.

## Building a container image
On the top directory, run following command:
```
docker build -t acmeair-driver -f docker/Dockerfile .
```

## Running the driver as a container
Make sure AcmeAir services are running.

In a terminal, run following command. The JMeter's output is shown in the terminal.
```
docker run -ti acmeair-driver
```

Some parameters can be passed to the script via environment variables by using `--env name=value` option:
```
docker run --env DURATION=600 --env THREADS=32 -ti acmeair-driver
```

Following table shows a list of available parameters:
|Name    |Description                   |Default value|
|:------:|:-----------------------------|:-----------:|
|HOST    |Host name of the ningx service|fs12.trl.ibm.com (my machine...)|
|PORT    |HTTP port of the ningx service|80|
|THREADS |Number of driver threads      |1|
|DURATION|Seconds to drive AcmeAir      |60|
|SUMMARY_INTERVAL|Interval (sec) to report summarizer statistics|10|


## Saving the summarizer log
To save the JMeter summarizer log, redirect the output of the container to a file, or copy the log file in the container by using `docker cp` command:
```
docker cp `docker ps -aqf ancestor=acmeair-driver -n=1`:/deployments/acmeair-driver.log .
```
