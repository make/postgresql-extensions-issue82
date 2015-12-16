dbProperties = [
	jmxEnabled : true,
	initialSize : 5,
	maxActive : 50,
	minIdle : 5,
	maxIdle : 25,
	maxWait : 10000,
	maxAge : 10 * 60000,
	timeBetweenEvictionRunsMillis : 10000,
	minEvictableIdleTimeMillis : 60000,
	validationQuery : "SELECT 1",
	validationQueryTimeout : 3,
	validationInterval : 15000,
	testOnBorrow : true,
	testWhileIdle : true,
	testOnReturn : false,
	defaultTransactionIsolation : java.sql.Connection.TRANSACTION_READ_COMMITTED
]
dataSources {
    dataSource {
        pooled = true
        driverClassName = 'org.postgresql.Driver'
        username = 'issue82'
        password = 'issue82'
        url = 'jdbc:postgresql://localhost/issue82'
        dialect = 'net.kaleidos.hibernate.PostgresqlExtensionsDialect'
        properties = dbProperties
        logSql = false
    }
}
