package com.qainfotech.tap.training.snl.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestBoard {

	Board board;

	@BeforeTest
	public void loadDb() throws FileNotFoundException, UnsupportedEncodingException, IOException, PlayerExistsException,
			GameInProgressException, MaxPlayersReachedExeption {
		board = new Board();
		board.registerPlayer("Somil");
		board.registerPlayer("Vaibhav");

	}

	@Test(priority = 0)
	public void testBoardParameterizedConstructor()
			throws IOException, PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption {
		UUID uuid = UUID.randomUUID();

		Board test = new Board(board.getUUID());

		assertThat(test.getData()).isNotNull();
		System.out.println(test.getData().getJSONArray("players"));
	}

	@Test(priority = 1)
	public void register_player()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException, InvalidTurnException, NoUserWithSuchUUIDException {

		assertThat(((JSONObject) board.getData().getJSONArray("players").get(0)).get("position")).isEqualTo(0);
		assertThat(((JSONObject) board.registerPlayer("Rohan").get(2)).get("name")).isEqualTo("Rohan");
	}

	@Test(expectedExceptions = PlayerExistsException.class, priority = 2)
	public void PlayerAlreadyExist() throws MaxPlayersReachedExeption, FileNotFoundException,
			UnsupportedEncodingException, PlayerExistsException, GameInProgressException, IOException {

		board.registerPlayer("Rohan");
	}

	@Test(expectedExceptions = MaxPlayersReachedExeption.class, priority = 3)
	public void MaxPlayerReached() throws MaxPlayersReachedExeption, FileNotFoundException,
			UnsupportedEncodingException, PlayerExistsException, GameInProgressException, IOException {
		board.registerPlayer("sumit");
		board.registerPlayer("rajan");
		assertThat(board.getData().getJSONArray("players").length()).isLessThanOrEqualTo(4);
	}

	@Test(priority = 4)
	public void PlayerStartFromPostionZero() {

		for (int index = 0; index < board.data.getJSONArray("players").length(); index++) {
			assertThat(Integer
					.parseInt(((JSONObject) board.data.getJSONArray("players").get(index)).get("position").toString()))
							.isEqualTo(0);
		}
	}

	@Test(priority = 5)
	public void DeleteRegisteredPlayer()
			throws FileNotFoundException, UnsupportedEncodingException, NoUserWithSuchUUIDException {
		UUID uuid1 = ((UUID) ((JSONObject) board.getData().getJSONArray("players").get(0)).get("uuid"));
		board.deletePlayer(uuid1);

		int flag = 0;
		for (int i = 0; i < board.data.getJSONArray("players").length(); i++) {
			UUID id = (UUID) board.data.getJSONArray("players").getJSONObject(i).get("uuid");
			if (uuid1.equals(id)) {
				flag = 1;
			}
		}
		assertThat(flag == 0);
	}

	@Test(expectedExceptions = NoUserWithSuchUUIDException.class, priority = 6)
	public void CheckInvalidTurn() throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException,
			GameInProgressException, MaxPlayersReachedExeption, IOException, NoUserWithSuchUUIDException {
		UUID uuid = UUID.randomUUID();
		board.deletePlayer(uuid);
	}

	/**
	 * Register player should throw game in progress exception when new player
	 * come in between game
	 */
	@Test(expectedExceptions = GameInProgressException.class, priority = 7)
	public void checkGameIsInProgress() throws InvalidTurnException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException {
		UUID uuid1 = ((UUID) ((JSONObject) board.getData().getJSONArray("players").get(0)).get("uuid"));
		assertThat(board.rollDice(uuid1));
		board.registerPlayer("Shivam");
	}

	@Test(expectedExceptions = InvalidTurnException.class, priority = 8)
	public void toCheckInvalidTurn()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException, InvalidTurnException, NoUserWithSuchUUIDException {

		board.rollDice((UUID) ((JSONObject) board.getData().getJSONArray("players").get(0)).get("uuid"));
		board.rollDice((UUID) ((JSONObject) board.getData().getJSONArray("players").get(1)).get("uuid"));
		board.rollDice((UUID) ((JSONObject) board.getData().getJSONArray("players").get(2)).get("uuid"));
		assertThat(board.getData().get("turn")).isEqualTo(2);
	}


	@Test(priority = 9)
	public void testRollDice() throws InvalidTurnException, IOException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption {

		Board board1 = new Board();
		board1.registerPlayer("Somil");
		board1.registerPlayer("Vaibhav");

		board1.registerPlayer("Amit");
		board1.registerPlayer("Sumit");

		Object ob;
		for (int index = 0; index < board.data.getJSONArray("players").length(); index++) {
			
			UUID uuid = UUID.fromString(board1.data.getJSONArray("players").getJSONObject(index).get("uuid").toString());
			Object playerObject = board1.getData().getJSONArray("players").getJSONObject(index);

			System.out.println(playerObject);

			JSONObject player = (JSONObject) playerObject;

			player.put("position", 97);
			Integer playerPositionBeforeRollDice = player.getInt("position");

			System.out.println("playerPositionBeforeRollDice " + playerPositionBeforeRollDice);
			JSONObject rollDiceObject = board1.rollDice(uuid);
			System.out.println("rollDiceObject" + rollDiceObject);

			int dice = rollDiceObject.getInt("dice");
			System.out.println("dice   " + dice);
			int playerPositionAfterRollDice = (int) board1.getData().getJSONArray("players").getJSONObject(index)
					.get("position");

			System.out.println("playerPositionAfterRollDice" + playerPositionAfterRollDice);
			int playerType = board1.getData().getJSONArray("steps").getJSONObject(playerPositionAfterRollDice)
					.getInt("type");

			System.out.println("playerType" + playerType);
			if (playerType == 1 && playerPositionAfterRollDice <= 100) {
				String msg = "Player was bit by a snake, moved back to " + playerPositionAfterRollDice;
				System.out.println(msg);
				assertThat(rollDiceObject.getString("message")).isEqualTo(msg);
			} 
			else if (playerType == 2 && playerPositionAfterRollDice <= 100) {
				String msg = "Player climbed a ladder, moved to " + playerPositionAfterRollDice;
				System.out.println(msg);
				assertThat(rollDiceObject.getString("message")).isEqualTo(msg);
			}
			else if (playerType == 0 && playerPositionAfterRollDice <= 100
					&& playerPositionAfterRollDice != playerPositionBeforeRollDice) {
				String msg = "Player moved to " + playerPositionAfterRollDice;
				System.out.println(msg);
				assertThat(rollDiceObject.getString("message")).isEqualTo(msg);
			}
			else if (playerPositionAfterRollDice > 100
					|| playerPositionAfterRollDice == playerPositionBeforeRollDice) {
				String msg = "Incorrect roll of dice. Player did not move";
				System.out.println(msg);
				assertThat(rollDiceObject.getString("message")).isEqualTo(msg);
			}
		}
	}
	
	
	@Test(priority = 10)
	public void TestDoesPlayerMoveToNewPosition_Test()
			throws FileNotFoundException, UnsupportedEncodingException, IOException, PlayerExistsException,
			GameInProgressException, MaxPlayersReachedExeption, InvalidTurnException {

		Board board2 = new Board();

		board2.registerPlayer("Shivam");
		board2.registerPlayer("Mohit");
		board2.registerPlayer("Shubham");
		board2.registerPlayer("Rajeev");

		for (int index = 0; index < board2.data.getJSONArray("players").length(); index++) {
			UUID uuid = UUID
					.fromString(board2.data.getJSONArray("players").getJSONObject(index).get("uuid").toString());

			Integer playerPositionBeforeRollDice = board2.getData().getJSONArray("players").getJSONObject(index)
					.getInt("position");
			System.out.println("TestplayerPositionBeforeRollDice " + playerPositionBeforeRollDice);
			JSONObject response = board2.rollDice(uuid);

			int playerPositionAfterRollDice = board2.getData().getJSONArray("players").getJSONObject(index)
					.getInt("position");
			System.out.println("TestplayerPositionAfterRollDice" + playerPositionAfterRollDice);

			assertThat(playerPositionBeforeRollDice).isNotEqualTo(playerPositionAfterRollDice);

		}
	}
	
	@Test(priority=11)
	public void testIncorrectRoleOfDice()
				throws FileNotFoundException, UnsupportedEncodingException, IOException, PlayerExistsException,
				GameInProgressException, MaxPlayersReachedExeption, InvalidTurnException {

		Board board3 = new Board();

		board3.registerPlayer("Shivam");
		board3.registerPlayer("Mohit");
		board3.registerPlayer("Shubham");
		board3.registerPlayer("Rajeev");
			
			for (int index = 0; index < board3.getData().getJSONArray("players").length(); index++) {
				board3.getData().getJSONArray("players").getJSONObject(index).put("position", 100);
			}
			
			UUID uuid = UUID
					.fromString(board3.data.getJSONArray("players").getJSONObject((int) board3.getData().get("turn")).get("uuid").toString());

			JSONObject response = board3.rollDice(uuid);
			assertThat(response.get("message")).isEqualTo("Incorrect roll of dice. Player did not move");
	
		}
	
	
	
	
		@Test(priority=12)
		public void isPlayerClimbedTest() throws FileNotFoundException, UnsupportedEncodingException, IOException,
				PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption, InvalidTurnException {

			Board board4 = new Board();

			board4.registerPlayer("Shivam");
			board4.registerPlayer("Mohit");
			board4.registerPlayer("Shubham");
			board4.registerPlayer("Rajeev");
			
			for (int value = 1; value < 7; value++) {
				JSONObject step =  board4.getData().getJSONArray("steps").getJSONObject(value);
				step.put("type", 2);
				step.put("target", value*5);
			}
			
			System.out.println(board4.getData().getJSONArray("steps").toString());
			for (int i = 0; i < 4; i++) {
				int turn = (int) board4.getData().get("turn");
				JSONObject player = board4.getData().getJSONArray("players").getJSONObject(turn);
				UUID uuid = UUID.fromString(player.get("uuid").toString());


				Integer playerPositionBeforeRollDice = player.getInt("position");
				
				JSONObject response = board4.rollDice(uuid);

				int playerPositionAfterRollDice = player.getInt("position");
				
				System.out.println("playerPositionBeforeRollDice " + playerPositionBeforeRollDice);
				System.out.println(response.get("dice"));
				System.out.println("playerPositionAfterRollDice" + playerPositionAfterRollDice);
				System.out.println(response.get("message"));
				System.out.println("\n");
				
							
				assertThat(board4.data.getJSONArray("steps").getJSONObject((int) response.get("dice")).get("target"))
						.isEqualTo(playerPositionAfterRollDice);
			}

		}

	
	
		@Test(priority=13)
	public void SnakeTest() throws FileNotFoundException, UnsupportedEncodingException, IOException,
				PlayerExistsException, GameInProgressException, MaxPlayersReachedExeption, InvalidTurnException {
		Board board5 = new Board();

		board5.registerPlayer("Shivam");
		board5.registerPlayer("Mohit");
		board5.registerPlayer("Shubham");
		board5.registerPlayer("Rajeev");
		
		JSONObject data = board.getData();
		JSONArray playerArray = data.getJSONArray("players");
		for (int index = 0; index <board5.getData().getJSONArray("players").length(); index++) {
			board5.getData().getJSONArray("players").getJSONObject(index).put("position", 70);
		}

		System.out.println(	board5.getData().getJSONArray("players"));
	
	
		int counter=1;
		for (int value = 71; value < 80; value++) {
			JSONObject step =  board5.getData().getJSONArray("steps").getJSONObject(value);
			step.put("type", 1);
			step.put("target", value/5 +counter);
			counter++;
		}
			for (int i = 71; i < 80; i++) {
				System.out.println(board5.data.getJSONArray("steps").getJSONObject(i).get("target"));
			}
	
	for (int i = 0; i < 4; i++) {
		int turn = (int) board5.getData().get("turn");
		JSONObject player = board5.getData().getJSONArray("players").getJSONObject(turn);
		UUID uuid = UUID.fromString(player.get("uuid").toString());


		Integer playerPositionBeforeRollDice = player.getInt("position");
		
		JSONObject response = board5.rollDice(uuid);

		int playerPositionAfterRollDice = player.getInt("position");
		
		System.out.println("playerPositionBeforeRollDice " + playerPositionBeforeRollDice);
		System.out.println(response.get("dice"));
		System.out.println("playerPositionAfterRollDice" + playerPositionAfterRollDice);
		System.out.println(response.get("message"));
		System.out.println("\n");
		
					
		assertThat(board5.getData().getJSONArray("steps").getJSONObject(((int) response.get("dice"))+70).get("target"))
				.isEqualTo(playerPositionAfterRollDice);
	}

		}
	
}
	
