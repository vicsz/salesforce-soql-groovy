# salesforce-soql-groovy
Simple facade over salesforce HTTP soql calls, modeled after groovy sql class.

Example usage:

```groovy
Soql soql = Soql.newInstance(
    url: "https://ompanyname.my.salesforce.com",
    username: "myUsername",
    password: "myPassword",
    securityToken: "mySecurityToken"
)

soql.eachRow("SELECT FirstName, LastName, Title FROM User ORDER BY LastName"){
    println "$it.LastName $it.FirstName $it.Title"
}

```