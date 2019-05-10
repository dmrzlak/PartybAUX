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

import org.junit.Test;

public class TestFollowerService {

	@Mock ClientService clntService;
	@Mock ClientRepository cRepo;
	@Mock FollowerService fllwService;
	@Mock FollowerRepository fRepo;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@After
	public void validate() {
		validateMockitoUsage();
	}
	
	@Test
	public void getAllFollowersServiceTest() {
		clntService.addClientService(cRepo, 1, 1, "marcin", "mlukanus@gmail.com", "howdy", 0, 0);
		clntService.addClientService(cRepo, 2, 2, "evan", "evan@gmail.com", "yeehaw", 0, 0);
		Client m = cRepo.findByUsername("marcin");
		Client e = cRepo.findByUsername("evan");
		List<Client> list = new ArrayList<Client>();
		list.add(m);
		list.add(e);
		
		when(cRepo.findAll()).thenReturn(list);
		List<Client> clientList = clntService.getAllClientsService(cRepo);
		
		FollowerService.addFollowerService(cRepo, fRepo, 0, "marcin", "evan");
		Follower f = fRepo.findByUsername("marcin");
		List<Follower> fList = new ArrayList<Follower>();
		fList.add(f);
		
		when(fRepo.findAll()).thenReturn(fList);
		List<Follower> followerList = fllwService.getAllFollowersService(fRepo);
		
		assertEquals(1, followerList.size());
		
		// ISSUE with this verify method
		//verify(fllwService).addFollowerService(cRepo, fRepo, 0, "marcin", "evan");
	}

}
