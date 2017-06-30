package com.qainfotech.tap.training.snl.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestBoard {

	Board board;

	@BeforeTest
	public void BoardTest() throws FileNotFoundException, UnsupportedEncodingException, IOException,
			PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption {
		board = new Board();
		board.registerPlayer("Somil");
		board.registerPlayer("vaibhav");
		board.registerPlayer("rohan");

	}

	@Test(expectedExceptions = PlayerExistsException.class, priority = 1)
	public void registerPlayerShouldThrowPlayerExistsExceptionForSameRegistration()
			throws MaxPlayersReachedExeption, FileNotFoundException, UnsupportedEncodingException,
			PlayerExistsException, GameInProgressException, IOException {
		board.registerPlayer("rohan");
	}

	@Test(expectedExceptions = MaxPlayersReachedExeption.class, priority = 2)
	public void registerPlayerShouldNotallowedMoreThanFourPlayer()
			throws MaxPlayersReachedExeption, FileNotFoundException, UnsupportedEncodingException,
			PlayerExistsException, GameInProgressException, IOException {
		board.registerPlayer("sumit");
		board.registerPlayer("rajan");
		assertThat(board.getData().getJSONArray("players").length()).isLessThanOrEqualTo(4);
	}

	
	  @Test(priority=3)
	  public void registerPlayerShouldStartFromPostionZero(){
	  
	  for(int index=0;index<board.data.getJSONArray("players").length();index++) {
	  assertThat(Integer.parseInt(((JSONObject)board.data.getJSONArray("players").get(index)).get("position").toString())).isEqualTo(0); 
	  }
	  }
	 
		
	
	  
	@Test(priority =4)
	public void deletePlayerShouldDeleteRegisteredPlayer()
			throws FileNotFoundException, UnsupportedEncodingException, NoUserWithSuchUUIDException {

		UUID uuid1 = ((UUID) ((JSONObject) board.getData().getJSONArray("players").get(0)).get("uuid"));
		board.deletePlayer(uuid1);

		int flag = 0;
		for (int i = 0; i < board.data.getJSONArray("players").length(); i++) {
			UUID id=(UUID) board.data.getJSONArray("players").getJSONObject(i).get("uuid");
			if(uuid1.equals(id)){
						flag = 1;
		}}
		assertThat(flag == 0);
	}
	

	  @Test(expectedExceptions=GameInProgressException.class, priority=5)
	  public void registerPlayerShouldThrowGameInProgressExceptionWhenNewPlayerComeInBetweenGame() throws InvalidTurnException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption, IOException{
		  UUID uuid1 = ((UUID) ((JSONObject) board.getData().getJSONArray("players").get(0)).get("uuid"));
        assertThat(board.rollDice(uuid1));	  
		  board.registerPlayer("Abhishek");
	  }
	  
	@Test(expectedExceptions=NoUserWithSuchUUIDException.class,priority=6)
	     public void delete_player_should_throw_No_User_With_Such_UUID_Exception_for_invalid_players() throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption, IOException, NoUserWithSuchUUIDException
	     {
		UUID uuid= UUID.randomUUID();
		board.deletePlayer(uuid);
	    	}
	
	
	

}
