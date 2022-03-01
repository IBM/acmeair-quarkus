#! /bin/bash
top=$(realpath $(dirname $0))

set -o pipefail

HOST=${HOST:-localhost}
PORT=${PORT:-80}

export JRE_HOME=${JRE_HOME:-/usr/lib/jvm/java-1.8.0-openjdk}
export PATH=$JRE_HOME/bin:$PATH

## Timezone must be UTC/GMT to make AcmeAir work correctly ##
export TZ=UTC


JMETER_HOME=${JMETER_HOME:-$top/apache-jmeter-5.4.1}
jmeter="$JMETER_HOME/bin/jmeter.sh"

jmeterArgs="-DusePureIDs=true -DCookieManager.save.cookies=true"
jmeterArgs="$jmeterArgs -n -t AcmeAir-microservices-mpJwt.jmx"
jmeterArgs="$jmeterArgs -JHOST=${HOST} -JPORT=${PORT}"
jmeterArgs="$jmeterArgs -JTHREAD=${THREADS:-1}"
jmeterArgs="$jmeterArgs -JDURATION=${DURATION:-60}"
jmeterArgs="$jmeterArgs -Jsummariser.interval=${SUMMARIZER_INTERVAL:-10}"

log=${top}/acmeair-driver.log
jtl=${top}/responses.jtl

if [[ -n "${LOG_RESPONSE}" ]]; then
   [[ -f "$jtl" ]] && mv -f "$jtl" "$jtl".bak
   jmeterArgs="$jmeterArgs -l $jtl"
   jmeterArgs="$jmeterArgs -Jjmeter.save.saveservice.output_format=xml"
   jmeterArgs="$jmeterArgs -Jjmeter.save.saveservice.samplerData=true"
   jmeterArgs="$jmeterArgs -Jjmeter.save.saveservice.response_data=true"
   jmeterArgs="$jmeterArgs -Jjmeter.save.saveservice.requestHeaders=true"
   jmeterArgs="$jmeterArgs -Jjmeter.save.saveservice.responseHeaders=true"
fi


loadDB()
{
   name="$1"
   shift

   echo "Initializing $name DB" | tee -a "${log%.log}.dbload"
   curl "$@" 2>&1 | tee -a "${log%.log}.dbload"
   rc=$?

   if [[ $(($rc - 0)) -ne 0 ]]; then
      echo "ERROR: Failed to load $name DB. 'curl' exit code is $rc"
      exit $rc
   fi

   echo -e "\n" | tee -a "${log%.log}.dbload"
}

sleep ${WAIT_TO_START:-1}

echo "*** Load DBs : $(date +'%F %T') ***"
loadDB Booking  "http://${HOST}:${PORT}/booking/loader/load"
loadDB Flight   "http://${HOST}:${PORT}/flight/loader/load"
loadDB Customer "http://${HOST}:${PORT}/customer/loader/load?numCustomers=10000"

sleep ${WAIT_BEFORE_JMETER:-5}

echo "*** Start JMeter : $(date +'%F %T') DURATION=${DURATION} ***"
(cd scripts; $jmeter $jmeterArgs -j $log)
echo "*** Finish JMeter : $(date +'%F %T') ***"

sleep ${WAIT_AFTER_JMETER:-5}

case "${LOG_RESPONSE}" in
   
[1-9]*)  echo -e "\n*** Request/response log (Last ${LOG_RESPONSE} lines) ***"
	 tail -n ${LOG_RESPONSE} $jtl
	 echo "*** End of request/response log (Last ${LOG_RESPONSE} lines) ***";;

[Pp]rint|[Ss]how|[Aa]ll)
         echo -e "\n*** Request/response log ***"
	 cat $jtl
	 echo "*** End of request/response log ***";;
esac

echo -e "\n=== Overall result ==="
grep ' INFO o.a.j.r.Summariser: summary = ' $log | tail -1

if [[ x"${LOG_RESPONSE}" = x[Ww]ait ]]; then
   tail -f /dev/null
fi
