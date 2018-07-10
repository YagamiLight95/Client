package com.client;

import java.time.Duration;
import java.util.ArrayList;

import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class MainClient {

	//static JedisPool jp;
	
	public static void main(String[] args) {
		
		System.out.println("Script partito...");
		try {
		//	jp = new JedisPool(buildPoolConfig(),"172.30.118.49", 6379);
			Jedis jClient = new Jedis("172.30.118.49", 6379); 
					//jp.getResource();
			System.out.println(System.getenv("REDIS_PWD"));
			jClient.auth(System.getenv("REDIS_PWD"));
			jClient.connect();
			System.out.println("Mi metto in ascolto sul canale Nuovi_Utenti");
			jClient.subscribe(new JedisPubSub() {
			    @Override
			    public void onMessage(String channel, String message) {
			    	String user ="";
			    	Jedis jClient1 = new Jedis("172.30.118.49", 6379);
					jClient1.auth(System.getenv("REDIS_PWD"));
					jClient1.connect();
			    	//System.out.println("Messaggio ricevuto : " + message + " sul canale : "+ channel);
		    		user = jClient1.rpop("Users");
		    		ArrayList<String> users = new ArrayList<String>();
		    		while(user!=null){
			    		users.add(user);
			    		user = jClient1.rpop("Users");
			    	}
		    		jClient1.disconnect();
		    		if(users.size()>0) {
		    			System.out.println(users.toString());
		    		}
			    }
			}, "Nuovi_Utenti");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	private static JedisPoolConfig buildPoolConfig() {
	    final JedisPoolConfig poolConfig = new JedisPoolConfig();
	    poolConfig.setMaxTotal(128);
	    poolConfig.setMaxIdle(128);
	    poolConfig.setMinIdle(16);
	    poolConfig.setTestOnBorrow(true);
	    poolConfig.setTestOnReturn(true);
	    poolConfig.setTestWhileIdle(true);
	    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
	    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
	    poolConfig.setNumTestsPerEvictionRun(3);
	    poolConfig.setBlockWhenExhausted(true);
	    return poolConfig;
	}
	
	*/
}
