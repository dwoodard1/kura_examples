package org.dwoodard.kura.example.mongodb;

import java.util.Map;

import org.bson.Document;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.configuration.Password;
import org.eclipse.kura.crypto.CryptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDbExample implements ConfigurableComponent {

	private static final Logger logger = LoggerFactory.getLogger(MongoDbExample.class);
	
	private String mongoURI;
	private String dbName;
	private CryptoService cryptoService;
	
	/*
	 * Dependencies
	 */
	public void setCryptoService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public void unsetCryptoService(CryptoService cryptoService) {
        this.cryptoService = null;
    }
	
	/*
	 * OSGi Lifecycle methods
	 */
	protected void activate() {
		logger.info("MongoDbExample activate...");
	}
	
	protected void deactivate() {
		logger.info("MongoDbExample deactivate...");
	}
	
	protected void updated(Map<String, Object> properties) {
		logger.info("MongoDbExample update...");
		
		String host = (String) properties.get("host");
		String user = (String) properties.get("user");
		Password password = null;
		try {
			password = new Password(
					this.cryptoService.decryptAes(((String) properties.get("password")).toCharArray()));
		} catch (KuraException e) {
			logger.error("Error: " + e.getLocalizedMessage());
		}
		this.dbName = (String) properties.get("dbName");
		
		if (password != null) {
			this.mongoURI = "mongodb://" + user + ":" + password.toString() + "@" + host + "/" + this.dbName;
			doWork();
		}
	}
	
	/*
	 * Private methods
	 */
	private void doWork() {
		// Connect to MongoDB
		MongoClientURI connectionString = new MongoClientURI(this.mongoURI);
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase db = mongoClient.getDatabase(this.dbName);
		
		// Create a collection and insert document
		MongoCollection<Document> collection = db.getCollection("test");
		Document document = new Document("name", "MongoTest")
			.append("type", "database")
			.append("count", 1);
		collection.insertOne(document);
		
		// Retrieve document to verify
		Document findDoc = collection.find().first();
		logger.info(findDoc.toJson());
		
		// Flush DB so this example can run next time
		//db.drop();
		
		// Close connection
		mongoClient.close();
		
	}
}
