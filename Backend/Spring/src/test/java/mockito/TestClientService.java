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
import org.springframework.partybauxserver.client.Client;
import org.springframework.partybauxserver.client.ClientRepository;
import org.springframework.partybauxserver.client.ClientService;
import org.springframework.partybauxserver.follower.*;

public class TestClientService {
	
	@Mock ClientService clntService;
	@Mock ClientRepository repo;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@After
	public void validate() {
		validateMockitoUsage();
	}

	@Test
	public void getAllClientsServiceTest() {
		clntService.addClientService(repo, 1, 1, "marcin", "mlukanus@gmail.com", "howdy", 0, 0);
		Client c = repo.findByUsername("marcin");
		List<Client> list = new ArrayList<Client>();
		list.add(c);
		
		when(repo.findAll()).thenReturn(list);
		
		List<Client> repoList = clntService.getAllClientsService(repo);
		
		assertEquals(1, repoList.size());
		//verify(clntService, times(1)).addClientService(repo, 1, 1, "marcin", "mlukanus@gmail.com", "howdy", 0, 0);
		verify(repo, times(1)).findAll();
	}
	
	@Test
	public void removeClientServiceTest() {
		clntService.addClientService(repo, 1, 1, "marcin", "mlukanus@gmail.com", "howdy", 0, 0);
		clntService.removeClientService(repo, 1);
		
		List<Client> repoList = clntService.getAllClientsService(repo);
		
		assertEquals(0, repoList.size());
		
		// ISSUE with this verify method
		verify(clntService).removeClientService(repo, 1); 
	}

}
