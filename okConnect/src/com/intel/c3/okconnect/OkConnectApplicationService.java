package com.intel.c3.okconnect;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.intel.stc.events.InviteRequestEvent;
import com.intel.stc.events.InviteResponseEvent;
import com.intel.stc.events.StcException;
import com.intel.stc.events.StcUpdateEvent;
import com.intel.stc.interfaces.IStcActivity;
import com.intel.stc.interfaces.StcConnectionListener;
import com.intel.stc.interfaces.StcUserListListener;
import com.intel.stc.lib.StcLib;
import com.intel.stc.slib.StcServiceInet;
import com.intel.stc.utility.StcApplicationId;
import com.intel.stc.utility.StcSocket;
import com.intel.stc.utility.StcUser;

public class OkConnectApplicationService extends StcServiceInet implements StcUserListListener, StcConnectionListener, IStcActivity {

	StcSocket				socket				= 		null;
	private Contact			selfContact			= 		null;
	Affiliations 			myAffiliations		= 		null;
	//boolean					inviteInProgress	=		false;
	OkConnectMainActivity	activity			=		null;
	ArrayList<UUID> 		usersSentInvite		=		new ArrayList<UUID>();
	ArrayList<Contact> 		contactsList		= 		new ArrayList<Contact>();
	ArrayList<StcUser>		userList			=		new ArrayList<StcUser>();
	//ArrayList<StcUser>		usersWithOurApp		=		new ArrayList<StcUser>();
	//ArrayList<StcUser>		inviteNeededQueue	=		new ArrayList<StcUser>();	

	//
	//StcServiceInet methods
	//
	
	@Override
	public StcApplicationId getAppId() {
		return OkConnectRegisterApplication.id;
	}

	@Override
	public StcConnectionListener getConnListener() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public String getServiceIntent() {
		return "com.intel.c3.okconnect.OkConnectApplicationService";
	}

	@Override
	protected void stcLibPrepared(StcLib arg0) {
		if (arg0 == null) { return; }
		arg0.setConnectionListener(this);
		arg0.setUserListListener(this);
		loadSelfContact();
		//activity.setSelfContact(selfContact);
		try {
			StcLib lib = getSTCLib();
			if (lib != null) {
				userListChanged(getSTCLib().getUserListWithAvatar());
			}
		} catch (StcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	protected void stcPlatformMissing() {
		// TODO Auto-generated method stub
		
	}
	
	//
	//StcUserListListener methods
	//
	
	@Override
	public void userListUpdated(StcUpdateEvent arg0) {
		try {
			userListChanged(getSTCLib().getUserListWithAvatar());
		} catch (StcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//StcConnectionListener methods

	@Override
	public void connectionCompleted(InviteResponseEvent arg0) {
		if (getSTCLib() == null) { return; }
		if (arg0.getStatus().equals(InviteResponseEvent.InviteStatus.sqtAccepted)) {
			
			try {
				socket = getSTCLib().getPreparedSocket(arg0.getSessionGuid(), arg0.getConnectionHandle());
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ObjectOutputStream out = null;
			
			if (socket != null) {
				try {
					out = new ObjectOutputStream(socket.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (out != null) {
				try {
					out.writeObject(selfContact);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		socket = null;
	}

	@Override
	public void connectionRequest(InviteRequestEvent arg0) {
		if (getSTCLib() == null) { return; }
			try {
				socket = getSTCLib().acceptInvitation(arg0.getSessionUuid(), arg0.getConnectionHandle());
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ObjectInputStream in = null;
			
			if (socket != null) {
				try {
					in = new ObjectInputStream(socket.getInputStream());
				} catch (StreamCorruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Contact newContact = null;
			
			if (in != null) {
				try {
					newContact = (Contact) in.readObject();
				} catch (OptionalDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (newContact != null) {
				contactsList.add(newContact);
				activity.contactsListChanged();
			}
			
		if (socket != null) {
			try {
				socket.close();
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		socket = null;
		inviteUser(arg0.getSessionUuid());
	}

	//
	//IStcActivity methods
	//
	
	@Override
	public void onStartClient(UUID arg0, int arg1) {
		//loadSelfContact();
	}

	@Override
	public void onStartNormal() {
		//loadSelfContact();
	}

	@Override
	public void onStartServer(UUID arg0) {
		//loadSelfContact();
	}
	
	//
	//okConnect custom methods
	//
	
	public void update () {
		this.loadSelfContact();
		//TODO: load affiliations
		try {
			StcLib lib = this.getSTCLib();
			if(lib != null)
				this.userListChanged(this.getSTCLib().getUserListWithAvatar());
		} catch (StcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exitService () {
		try {
			socket.close();
		} catch (StcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket = null;
		usersSentInvite = null;
		stopSelf();
	}
	
	public void inviteUser (UUID uuid) {
		if (!usersSentInvite.contains(uuid)) {
			try {
				getSTCLib().inviteUser(uuid, (short) 20);
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			usersSentInvite.add(uuid);
		}
	}
	
	private void userListChanged(List<StcUser> list) {
		list = limitUsersByApp(list);
		list = determineInvitesNeeded(list);
		for (StcUser user : list) {
			if (user != null) {
				inviteUser(user.getSessionUuid());
			}
		}
	}
	
	private List<StcUser> limitUsersByApp (List<StcUser> list) {
		List<StcUser> result = list;
		if (result.size() == 0) { return result; }
		if (getSTCLib() != null) {
			for (int i = 0; i < result.size(); i++) {
				StcUser user = result.get(i);
				if (!user.isAvailable()) {
					synchronized (result) {
						result.remove(i);
					}
				}
				else {
					UUID userApps [] = user.getAppList();
					boolean foundApp = false;
					for (UUID userApp : userApps) {
						if (userApp.toString().compareToIgnoreCase(OkConnectRegisterApplication.APP_UUID) == 0) {
							foundApp = true;
							break;
						}
					}
					if (!foundApp) {
						synchronized (result) {
							result.remove(i);
						}
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	private List<StcUser> determineInvitesNeeded(List <StcUser> list) {
		if (list.size() == 0) { return list; }
		if (getSTCLib() != null) {
			UUID mySessionUUID = null;
			try {
				mySessionUUID = getSTCLib().queryLocalUser().getSessionUuid();
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < list.size(); i++) {
				StcUser user = list.get(i);
				for (Contact contact : contactsList) {
					if (user.getUserUuid().compareTo(contact.getId()) == 0) {
						synchronized (list) {
							list.remove(i);
						}
					}
				}
				if (list.size() == 0) { return list; }
				if (mySessionUUID.compareTo(user.getSessionUuid()) < 0) {
					synchronized (list) {
						list.remove(i);
					}
				}
			}
		}
		return list;
	}
	
	public void loadSelfContact () {
		if (selfContact != null) { return; }
		try {
			selfContact = Contact.readFromFile(this, getString(R.string.self_contact_file_name));
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (selfContact == null) {
			selfContact = this.generateStandardSelfContact();
			try {
				Contact.writeToFile(getBaseContext(), getString(R.string.self_contact_file_name), selfContact);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Contact generateStandardSelfContact () {
		Contact result = null;
		String name = "";
		String email = "";
		UUID id = null;
		List<Interest> list = null;
		Bitmap avatar = null;
		if (getSTCLib() != null) {
			try {
				name = getSTCLib().queryLocalUser().getUserName();
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				id = getSTCLib().queryLocalUser().getUserUuid();
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				avatar = getSTCLib().queryLocalUser().getAvatar();
			} catch (StcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result = new Contact (name, id, list, email, avatar);
		}
		
		return result;
	}
	
	public void setSelfContact (Contact contact) {
		this.selfContact = contact;
	}
	
	public Contact getSelfContact () {
		return selfContact;
	}
	
	public void setActivity (Activity activity) {
		this.activity = (OkConnectMainActivity) activity;
	}
	
	public ArrayList<Contact> getContactsList() {
		return this.contactsList;
	}
}
