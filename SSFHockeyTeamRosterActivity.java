package com.SixthSenseFantasy.SSFHockey;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
/**
 * The activity class for the hockey team roster. The team name is displayed and editable. List of players on 
 * the team are displayed. Button to add players to the team. 
 * 
 * @author Nikolas Oliveira
 *
 */
public class SSFHockeyTeamRosterActivity  extends SSFHockeyActivity{
	SSFHockeyTeam team;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.team_roster);
		// fetch bundle from intent containing the team ID form calling activity
		Bundle bundle = getIntent().getExtras();
		String teamID = (String) bundle.getString("teamID");
		// fetch json for team, deserialize it and return SSFHockeyTeam object
		team = unpackTeam(teamID);
		// initialize team name text box
		intitTeamNameTextBox(team);
		initAddPlayerButton();
		// Note: The player list roster view is initialized and populated in onResume()
		// which is called by OnCreate() by default 
	}

	@Override
	public void onResume(){
		super.onResume();
		// get the latest version of the team roster, to insure most up to date roster is shown 
		//  when browsing back from other activities 
		team = SSFHockeyTeam.getTeam(team.getTeamId(), this.getApplicationContext());
		List<Integer> player_ids = team.getPlayerIDList();
		if (player_ids != null)
		{
			List<String> player_names = new ArrayList<String>();
			for (int player : player_ids){
				SSFHockeyPlayer tempPlayer = new SSFHockeyPlayer(player);
				// fetch data for player and parse into object
				tempPlayer.Parse(this.getApplicationContext());
				// add player name to list
				player_names.add(tempPlayer.getName());
				//TODO: cache this data
			}
			// initialize and display roster view
			initRosterList(player_names);
		}
	}
		
	/**
	 * initialize and set click listener for Add Player Button
	 */
	private void initAddPlayerButton() {
		Button addPlayerButton = (Button) findViewById(R.id.roster_add_player_button);
		addPlayerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(SSFHockeyTeamRosterActivity.this, 
						SSFHockeyPlayersActivity.class);
				SSFHockeyTeamRosterActivity.this.startActivity(myIntent);
			}
		});
		addPlayerButton.setEnabled(true);
	}

	/**
	 * initialize and display roster list view
	 * @param player_names list of player names to add to roster list view
	 */
	private void initRosterList(List<String> player_names) {
			ListView my_listview = (ListView)findViewById(R.id.roster_list);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, player_names);
			my_listview.setAdapter(adapter);
	}

	/**
	 * Checks if there is a team created for teamID and deserializes it, if 
	 * not creates new blank team 
	 * @param teamID ID of the team
	 * @return returns a SSFHockeyTeam
	 */
	private SSFHockeyTeam unpackTeam(String teamID) {
		SSFHockeyTeam team = SSFHockeyTeam.getTeam(teamID, this.getApplicationContext());
		return team;	
	}

	/**
	 * Initializes the team name text box
	 * @param team The team for which this roster view is for.
	 */
	private void intitTeamNameTextBox(SSFHockeyTeam team) {
		EditText nameTextBox = (EditText) findViewById(R.id.team_name_box);
		// initialize text change listener
		nameTextBox.addTextChangedListener(teamNameTextWatcher);
		// insert name or hint into the text box
		if(team.getTeamName().equals(""))
			nameTextBox.setHint("Enter Team Name Here");	
		else 
			nameTextBox.setText(team.getTeamName());
	}

	/**
	 * Text filterer watches for text changes in the search box
	 * and filters the list. 
	 */
	private TextWatcher teamNameTextWatcher = new  TextWatcher() {

		public void afterTextChanged(Editable s){	
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String temp = s.toString();
			team.setTeamName(temp);
			// method call to save team, because application context is not available inside TextWatcher class
			saveTeam(team);
		}
	};

	protected void saveTeam(SSFHockeyTeam team) {
		SSFHockeyTeam.saveTeam(team, this.getApplicationContext());
	}

}
