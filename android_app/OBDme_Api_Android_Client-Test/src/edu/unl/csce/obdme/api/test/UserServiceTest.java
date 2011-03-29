package edu.unl.csce.obdme.api.test;

import java.util.Collection;

import android.test.AndroidTestCase;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.api.entities.User;
import edu.unl.csce.obdme.api.entities.UserVehicle;
import edu.unl.csce.obdme.client.http.handler.BasicCollectionHandler;
import edu.unl.csce.obdme.client.http.handler.BasicObjectHandler;

public class UserServiceTest extends AndroidTestCase {
	private String apiKey;
	private ObdMeService service;
	
	@Override
	protected void setUp() throws Exception {
		apiKey = "abcd";
		service = new ObdMeService(apiKey);
	}
	
	public void testGetUserByEmailAsync() {
		final String email = "farmboy30@gmail.com";
		final Object lock = new Object();
		
		final BasicObjectHandler<User> handler = new BasicObjectHandler<User>(User.class) {

			@Override
			public void onCommException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void onObdmeException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void onOperationCompleted(User result) {
				assertEquals(result.getEmail(), email);
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		};
		service.getUsersService().getUserByEmailAsync(email, handler);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e1) {
				fail(e1.getMessage());
			}
		}		
	}		
	
		public void testAddUserAsync() {
			final String email = "farmboy30@gmail.com";
			final String pw = "qwerty";
			final Object lock = new Object();
			BasicObjectHandler<User> handler = new BasicObjectHandler<User>(User.class) {
				
				@Override
				public void onOperationCompleted(User result) {
					assertNotNull(result);
					assertEquals(result.getEmail(), email);
					assertTrue(result.getId() > 0L);
					synchronized (lock) {
						lock.notifyAll();
					}				
				}
	
				@Override
				public void onCommException(String message) {
					fail(message);
					synchronized (lock) {
						lock.notifyAll();
					}				
				}

				@Override
				public void onObdmeException(String message) {
					assertTrue(message.contains(email));
					synchronized (lock) {
						lock.notifyAll();
					}
					
				}
			};
			service.getUsersService().createUserAsync(email, pw, handler);
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}		
		}
		
		
		public void testGetUserNotExist() {
			final String email = "farmboy30000000000000000@gmail.com";
			final Object lock = new Object();
			BasicObjectHandler<User> handler = new BasicObjectHandler<User>(User.class) {
				@Override
				public void onCommException(String message) {
					fail(message);
					synchronized (lock) {
						lock.notifyAll();
					}
				}
				@Override
				public void onObdmeException(String message) {
					assertTrue(message.contains(email));
					synchronized (lock) {
						lock.notifyAll();
					}
				}
				@Override
				public void onOperationCompleted(User result) {
					fail("User should not exist");
					synchronized (lock) {
						lock.notifyAll();
					}
				}
			};
			service.getUsersService().getUserByEmailAsync(email, handler);
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
		}
		
		public void testGetUserByEmailFailAsync() {
			final String rightEmail = "farmboy30@gmail.com";
			final String wrongEmail = "farmboy30@gmail.comm";
			final Object lock = new Object();
		
			BasicObjectHandler<User> handler = new BasicObjectHandler<User>(User.class) {
				@Override
				public void onCommException(String message) {
					fail(message);
					synchronized (lock) {
						lock.notify();
					}
				}
				@Override
				public void onObdmeException(String message) {
					fail(message);
					synchronized (lock) {
						lock.notify();
					}
				}
				@Override
				public void onOperationCompleted(User result) {
					assertFalse(result.getEmail().equals(wrongEmail));
					synchronized (lock) {
						lock.notify();
					}				
				}
				
			};
			service.getUsersService().getUserByEmailAsync(rightEmail, handler);
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}	
			}
		}
		
		public void testAddVehicleToUser() {
			final String email = "farmboy30@gmail.com";
			final String VIN = "1234ABC123";
			final String alias = "Curtis' Car";
			final Object lock = new Object();
	
			BasicObjectHandler<UserVehicle> handler = new BasicObjectHandler<UserVehicle>(UserVehicle.class) {
				@Override
				public void onCommException(String message) {
					fail(message);
					synchronized (lock) {
						lock.notifyAll();
					}
				}
	
				@Override
				public void onObdmeException(String message) {
					fail(message);
					synchronized (lock) {
						lock.notifyAll();
					}
				}
	
				@Override
				public void onOperationCompleted(UserVehicle result) {
					assertEquals(result.getVIN(), VIN);
					assertEquals(result.getAlias(), alias);
					assertTrue(result.getVehicleId() > 0L);
					synchronized (lock) {
						lock.notifyAll();
					}				
				}
			};		
			service.getVehicleService().addUpdateVehicleToUserAsync(VIN, email, alias, handler);
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
		}
	
	public void testGetVehiclesForUserAsync() {
		final String email = "farmboy30@gmail.com";
		final Object lock = new Object();  
		BasicCollectionHandler<UserVehicle> handler = new BasicCollectionHandler<UserVehicle>(UserVehicle.class) {

			@Override
			public void onOperationCompleted(Collection<UserVehicle> result) {
				assertNotNull(result);
				assertTrue(result.size() > 0);
				synchronized (lock) {
					lock.notifyAll();
				}				
			}

			@Override
			public void onCommException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void onObdmeException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		};
		service.getVehicleService().getVehiclesForUserAsync(email, handler);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
	}
	
	public void testUserCredentialsInvalidAsync() {
		final String email = "farmboy30@gmail.com";
		final String pw = "qwertY";
		final Object lock = new Object();
		BasicObjectHandler<User> handler = new BasicObjectHandler<User>(User.class) {

			@Override
			public void onCommException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void onObdmeException(String message) {
				assertTrue(message.length() > 0);
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void onOperationCompleted(User result) {
				fail("User should not have validated.");
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		};
		service.getUsersService().validateUserCredentials(email, pw, handler);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
	}
	
	public void testUserCredentialsValidAsync() {
		final String email = "farmboy30@gmail.com";
		final String pw = "qwerty";
		final Object lock = new Object();
		BasicObjectHandler<User> handler = new BasicObjectHandler<User>(User.class) {

			@Override
			public void onCommException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void onObdmeException(String message) {
				fail(message);
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void onOperationCompleted(User result) {
				assertNotNull(result);
				assertEquals(email, result.getEmail());
				assertTrue(result.getId() > 0L);
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		};
		service.getUsersService().validateUserCredentials(email, pw, handler);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
	}


}
