/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package riakdemo;

import com.basho.riak.client.api.cap.ConflictResolverFactory;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

public class Main {

	public static void main(String[] args) {		
		try {	
			RiakObject object = new RiakObject().setContentType("text/plain")
					.setValue(BinaryValue.create("Systemy Rozproszone Duzej Skali"));
			
			Namespace bucket = new Namespace("Produkty");
			Location location = new Location(bucket, "SRDS");
			
			StoreValue storeOp = new StoreValue.Builder(object).withLocation(location).build();
			UsersSession.getClient().execute(storeOp);
			System.out.println("Object storage operation successfully completed");

			FetchValue fetchOp = new FetchValue.Builder(location).build();
			RiakObject fetchedObject = UsersSession.getClient().execute(fetchOp).getValue(RiakObject.class);
			assert (fetchedObject.getValue().equals(object.getValue()));
			System.out.println("Success! The object we created and the object we fetched have the same value");

			DeleteValue deleteOp = new DeleteValue.Builder(location).build();
			UsersSession.getClient().execute(deleteOp);
			System.out.println("Object successfully deleted");

			ConflictResolverFactory factory = ConflictResolverFactory.getInstance();
			factory.registerConflictResolver(Item.class, new ItemResolver());
			
			UsersSession session = UsersSession.getSession();
			
//			User[] users = new User[4];
//
//			users[0] = new User("PP", "Adam", 609, "A St");
//			users[1] = new User("PP", "Ola", 509, null);
//			users[2] = new User("UAM", "Ewa", 720, "B St");
//			users[3] = new User("PP", "Kasia", 713, "C St");
//
//
//			Namespace usersBucket = new Namespace("users");
//
//			for (User user : users) {
//				session.upsertUser(usersBucket, user.name, user);
//			}
//
//			for (User user : users) {
//				User fetchedUser = session.fetchUser(usersBucket, user.name);
//				System.out.println(fetchedUser);
//			}
//
//			for (User user : users) {
//				session.deleteUser(usersBucket, user.name);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
