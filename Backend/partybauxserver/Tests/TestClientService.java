
package mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.partybauxserver.client.Client;
import com.example.partybauxserver.client.ClientRepository;
import com.example.partybauxserver.client.ClientService;
import com.example.partybauxserver.follower.*;

public class TestClientService {

	@Mock
	ClientService clntService;

	@Mock
	ClientRepository repo;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void validate() {
	//validateMockitoUsage(); 
	}
	
	//Evan
	@Test
	public void removeDNEClientServiceTest() {
		//clntService.addClientService(repo, 1, "marcin", "mlukanus@gmail.com", "howdy", 0, 0);
		assertEquals(-1, clntService.removeClientService(repo, 0));
	}
	
	//Evan
	@Test
	public void getaddClientServiceTest() {

		when(repo.existsByEmail("mlukanus@gmail.com")).thenReturn(true);
		when(repo.existsByUsername("marcin")).thenReturn(true); // //repo.save(c);

		//System.out.println(repo.existsByEmail("mlukanus@gmail.com"));
		
		assertEquals("Invalid email accepted", -1, clntService.addClientService(repo, 2, "marcin1", "mlukanus@gmail.com", "howdy", 0, 0));
		assertEquals("Invalid username accepted", -2, clntService.addClientService(repo, 3, "marcin", "m1lukanus@gmail.com", "howdy", 0, 0));
		verify(repo, times(1)).existsByEmail("mlukanus@gmail.com");
	}
	
	//Marcin
	@Test
	public void getAllClientsServiceTest() {

		clntService.addClientService(repo, 1, "marcin", "mlukanus@gmail.com", "howdy", 0, 0);
		Client c = repo.findByUsername("marcin");
		List<Client> list = new ArrayList<Client>();
		list.add(c);

		when(repo.findAll()).thenReturn(list);

		List<Client> repoList = clntService.getAllClientsService(repo);

		assertEquals(1, repoList.size()); // verify(clntService,times(1)).addClientService(repo, 1, "marcin",
											// "mlukanus@gmail.com", "howdy", 0, 0); verify(repo, times(1)).findAll(); }

	}
	
	//Marcin
	@Test
	public void removeClientServiceTest() {
		clntService.addClientService(repo, 1, "marcin", "mlukanus@gmail.com", "howdy", 0, 0);
		clntService.removeClientService(repo, 1);

		List<Client> repoList = clntService.getAllClientsService(repo);

		assertEquals(0, repoList.size());

		// ISSUE with this verify method
		verify(clntService).removeClientService(repo, 1);
	}

}
 