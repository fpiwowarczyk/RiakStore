package riakdemo;

import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;

public class UsersSession {

	private static final Logger logger = LoggerFactory.getLogger(UsersSession.class);

	public static final String DEFAULT_CONTACT_POINT = "150.254.32.154";
	public static final int DEFAULT_CONTACT_POINT_PORT = 8087;

	public static UsersSession instance = null;

	private static RiakClient client;

	public static UsersSession getSession() throws UnknownHostException {
		if (instance != null)
			return instance;

		synchronized (UsersSession.class) {
			if (instance == null)
				instance = new UsersSession(null, 0);
		}

		return instance;
	}

	public static RiakClient getClient() throws UnknownHostException {
		if (client != null)
			return client;

		getSession();
		return client;
	}

	public UsersSession(String contactPoint, int contactPointPort) throws UnknownHostException {
		if (contactPoint == null || contactPoint.isEmpty()) {
			contactPoint = DEFAULT_CONTACT_POINT;
			contactPointPort = DEFAULT_CONTACT_POINT_PORT;
		}

		client = RiakClient.newClient(contactPointPort, contactPoint);
	}


	public void upsertItem(Namespace namespace, String key, String item) throws ExecutionException, InterruptedException {
		Location location = new Location(namespace, key);
		FetchValue fetchOp = new FetchValue.Builder(location).build();
		Item fetchedItem = client.execute(fetchOp).getValue(Item.class);

		fetchedItem.prodId = fetchedItem.prodId +" " + item+":"+ UUID.randomUUID().toString();
		StoreValue storeOp = new StoreValue.Builder(fetchedItem).withLocation(location).build();

		client.execute(storeOp);

		logger.info("Entry " + namespace.getBucketNameAsString() + "/" + key + " upserted");
	}



	public Item fetchItem(Namespace namespace, String key) throws ExecutionException, InterruptedException {


		Location location = new Location(namespace, key);
		FetchValue fetchOp = new FetchValue.Builder(location).build();
		Item fetchedItem = client.execute(fetchOp).getValue(Item.class);

		logger.info("Entry " + namespace.getBucketNameAsString() + "/" + key + " fetched");

		return fetchedItem;
	}

	public void deleteItem(Namespace namespace, String key,String itemUUID) throws ExecutionException, InterruptedException {
		Location location = new Location(namespace, key);
		FetchValue fetchOp = new FetchValue.Builder(location).build();
		Item fetchedItem = client.execute(fetchOp).getValue(Item.class);

		String[] list = fetchedItem.prodId.split(" ");

		String newProdId="";
		String deletedItem ="";
		for(String item : list) {
			String[] keyValue = item.split(":");
			if(!keyValue[1].equals(itemUUID)&& !newProdId.equals("")){
				newProdId += " "+item;
			} else if(!keyValue[1].equals(itemUUID)&& newProdId.equals("")){
				newProdId+= item;
			} else {
				deletedItem =item;
			}
		}

		fetchedItem.tombstone +=deletedItem +" ";
		fetchedItem.prodId = newProdId;

		StoreValue storeOp = new StoreValue.Builder(fetchedItem).withLocation(location).build();
		client.execute(storeOp);

		logger.info("Entry " + namespace.getBucketNameAsString() + "/" + key + " deleted");
	}



	protected void finalize() {
		try {
			if (client != null) {
				client.shutdown();
			}
		} catch (Exception e) {
			logger.error("Could not close existing cluster", e);
		}
	}

}
