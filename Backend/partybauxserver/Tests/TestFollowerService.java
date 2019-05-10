
  package mockito;
  
  import static org.junit.Assert.*; import static org.mockito.Mockito.times;
  import static org.mockito.Mockito.validateMockitoUsage; import static
  org.mockito.Mockito.verify; import static org.mockito.Mockito.when;
  
  import java.util.ArrayList; import java.util.List;
  
  import org.junit.After; import org.junit.Before; import org.junit.Test;
  import org.mockito.InjectMocks; import org.mockito.Mock; import
  org.mockito.MockitoAnnotations; import
  com.example.partybauxserver.client.Client; import
  com.example.partybauxserver.client.ClientRepository; import
  com.example.partybauxserver.client.ClientService; import
  com.example.partybauxserver.follower.*;
  
  import org.junit.Test;
  
  public class TestFollowerService {
  
  @Mock ClientService clntService;
  
  @Mock ClientRepository cRepo;
  
  @Mock FollowerService fllwService;
  
  @Mock FollowerRepository fRepo;
  
  
  
  
  @Before public void init() { MockitoAnnotations.initMocks(this); }
  
  @After public void validate() { validateMockitoUsage(); }
  //Marcin
  @Test public void getAllFollowersServiceTest() {
  clntService.addClientService(cRepo, 1, "marcin", "mlukanus@gmail.com", "howdy", 0, 0); 
  clntService.addClientService(cRepo, 2, "evan", "evan@gmail.com", "yeehaw", 0, 0);
  Client m = cRepo.findByUsername("marcin");
  Client e = cRepo.findByUsername("evan"); 
  List<Client> list = new
  ArrayList<Client>(); list.add(m); list.add(e);
  
  when(cRepo.findAll()).thenReturn(list); 
  List<Client> clientList =  clntService.getAllClientsService(cRepo);
  
  FollowerService.addFollowerService(cRepo, fRepo, 0, "marcin", "evan");
  Follower f = fRepo.findByUsername("marcin");
  List<Follower> fList = new ArrayList<Follower>();
  fList.add(f);
  
  when(fRepo.findAll()).thenReturn(fList); 
  List<Follower> followerList = fllwService.getAllFollowersService(fRepo);
  
  assertEquals(1, followerList.size());
  //Use Enumerator 
  }
  
  // ISSUE with this verify method //
  //verify(fllwService).addFollowerService(cRepo, fRepo, 0, "marcin", "evan"); }
  //Evan
  @Test public void getNumberofFollowers() {
  
  
  // Might be an issue with the findBy stuff, unsure if I did that right oops^^
  when(cRepo.existsByUsername("marcin")).thenReturn(true);
  Client marcin = new Client();
  marcin.setClient_id(1); 
  marcin.setUsername("marcin");
  marcin.setfollowerFK(1);
  when(cRepo.findByUsername("marcin")).thenReturn(marcin);
  List<Follower> Followers = new ArrayList<Follower>(); 
  Follower f = new Follower();
  f.setclientFK(1);
  Followers.add(f);
  f = new Follower();
  f.setclientFK(1);
  Followers.add(f);
  when(fRepo.findByclientFK(1)).thenReturn(Followers);
  
  List<Follower> followerList = FollowerService.getFollowers(cRepo, fRepo, "marcin");
  
  assertEquals(2, followerList.size()); }
  
  }
  
 