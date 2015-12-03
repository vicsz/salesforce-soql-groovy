package groovyx.net.sforce.soql

import com.sforce.soap.partner.Connector
import com.sforce.ws.ConnectorConfig
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

public class Soql {

    private final static String AUTHENTICATION_END_POINT = "https://login.salesforce.com/services/Soap/u/35.0/"
    private final static String QUERY_PATH = "services/data/v35.0/query"

    private String sessionId;
    private String url;

    private Soql(Map<String, String> args){

        ConnectorConfig config = new ConnectorConfig(
                username: args.username,
                password: args.password+args.securityToken,
                authEndpoint: AUTHENTICATION_END_POINT
        )

        Connector.newConnection(config)

        this.sessionId = config.sessionId;
        this.url = args.url;
    }

    public static Soql newInstance(Map<String, String> args)  {
        return new Soql(args)
    }

    private List<Object> rows(String soql){
        new HTTPBuilder().request(
                "${url}/${QUERY_PATH}?q=${URLEncoder.encode(soql,"UTF-8")}",
                Method.GET,
                ContentType.JSON)
        {
            headers['Authorization'] = "Bearer $sessionId"
            response.success = { response, reader ->
                return reader.records
            }
            response.'400' = { response, reader ->
                throw new IllegalArgumentException("$reader.errorCode $reader.message")
            }
        }
    }

    public void eachRow(String soql, Closure rowClosure){
        rows(soql).each {
            rowClosure.call(it)
        }
    }

    public void firstRow(String soql, Closure closure){
        List<Object>  result = rows(soql)
        if(result.size() > 0)
            closure.call(result[0])
    }
}
