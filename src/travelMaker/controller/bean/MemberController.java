package travelMaker.controller.bean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import portfolio.service.bean.GameService;
import travelMaker.model.dto.GroupSpaceDTO;
import travelMaker.model.dto.LandmarkBoardDTO;
import travelMaker.model.dto.LandmarkLikedDTO;
import travelMaker.model.dto.LargePosDTO;
import travelMaker.model.dto.SmallPosDTO;
import travelMaker.model.dto.TmUserDTO;
import travelMaker.model.dto.UserRkDTO;
import travelMaker.service.bean.CommentService;
import travelMaker.service.bean.LandmarkService;
import travelMaker.service.bean.MemberService;
import travelMaker.service.bean.TravelService;

@Controller
@RequestMapping("/mem/")
public class MemberController {
	
	@Autowired
	private MemberService memService = null;
	@Autowired
	private TravelService travelService = null;
	@Autowired
	private LandmarkService landmarkService = null;
	@Autowired
	private CommentService commentService = null;
	@Autowired
	private GameService gameService = null;
	
	@RequestMapping("index.fl")
	public String index(Model model) {
		//쿠키 가져오기 
		Map cooMap = memService.getAllCookies();
		//로그인 분기처리할 변수 가져오기
		int check = memService.checkCookie(cooMap);
		
		model.addAttribute("cooMap", cooMap);
		model.addAttribute("check", check);
		
		return "tm/client/index";
	}
	
	//404에러페이지
	@RequestMapping("error404page.fl")
	public String error404() {
		return "tm/client/error404";
	}
	
	//500에러페이지
	@RequestMapping("error500page.fl")
	public String error500() {
		return "tm/client/error500";
	}
	
	//회원가입 폼 페이지 
	@RequestMapping("signupForm.fl")
	public String signupForm() {
		return "tm/client/member/signupForm";
	}
	
	//회원가입 처리
	@RequestMapping("signupPro.fl")
	public String signupPro(TmUserDTO dto) {
		memService.addMember(dto);
		gameService.addWallet(dto.getId());
		return "tm/client/member/signupPro";
	}
	
	//로그인 폼
	@RequestMapping("loginForm.fl")
	public String loginForm() {
		return "tm/client/member/loginForm";
	}
	
	//로그인 처리
	@RequestMapping("loginPro.fl")
	public String loginPro(TmUserDTO dto, Model model, String auto) {
		//id, pw 확인
		int result = memService.idPwCheck(dto);
		//자동 로그인 시 쿠키 처리
		memService.addCookie(dto,auto);
		//뷰에 로그인 결과 넘겨주기 
		model.addAttribute("result",result);
		
		return "tm/client/member/loginPro";
	}
	
	//로그아웃
	@RequestMapping("logout.fl")
	public String logout(TmUserDTO dto, String auto) {
		//세션 지워주기 
		memService.removeSession("memId");
		memService.removeSession("memColor");
		memService.removeSession("memSkin");
		memService.removeSession("memIcon");
		//자동 로그인 했다면 쿠키 지워 주기 
		memService.removeCookie(dto, auto);
		
		return "redirect:index.fl";
	}
	
	//아이디 찾기 
	@RequestMapping("findIdForm.fl")
	public String findIdForm() {
		
		return "tm/client/member/findIdForm";
	}
	
	//아이디 찾기 
	//findIdForm에서 입력한 email을 받아와서 이 email의 아이디를 보여줘야함 
	@RequestMapping("findIdPro.fl")
	public String findIdPro(String email, Model model)throws Exception {
		TmUserDTO mem = memService.emailCheck(email);
		String comId=null;
		if(mem!=null) {
			String id = mem.getId();
			comId = memService.idStar(id);
		}
		model.addAttribute("mem", mem);
		model.addAttribute("comId", comId);
		
		return "tm/client/member/findIdPro";
	}
	
	//비로그인 일 때 비밀번호 찾기 form
	@RequestMapping("findPw.fl")
	public String findPw() {
		
		return "tm/client/member/findPw";
	}
	
	
	//pos 대분류 선택
	@ResponseBody
	@RequestMapping("ajaxModiPw.fl")
	public String ajaxModiPw(@RequestBody Map<Object,Object> map) throws Exception {
		String result = "";
		String id = (String)map.get("id");
		String newPw = (String)map.get("pw");
		System.out.println(id);
		//System.out.println(newPw);
		//id와 newPw 넘겨주면 pw랑 맞는지 확인 !
		int check = memService.exPwCheck(id, newPw);	//1이면 이미 존재, 0이면 존재 X
		System.out.println(check);
		if(check == 1) {
			result = "이전 비밀번호와 동일합니다";
		}else {
			result = "사용가능";
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(result);
		
		return  json;
	}
	//비밀번호 찾기 Pro
	//아이디,비밀번호 맞는지 확인
	//맞다면 비밀번호 변경 가능
	@RequestMapping("modiPwForm.fl")
	public String findPwPro(TmUserDTO mem,Model model) {
		int result = memService.idEmailCheck(mem);
		model.addAttribute("result", result);
		model.addAttribute("mem", mem);
		return "tm/client/member/modiPwForm";
	}
	
	//비밀번호 재설정 Pro
	@RequestMapping("modiPwPro.fl")
	public String modiPwPro(TmUserDTO mem,Model model) {
		//비밀번호 업데이트 하는 메서드 
		memService.pwChange(mem);
		return "redirect:index.fl";
	}
	
	//회원 정보 수정
	@RequestMapping("myModi.fl")
	public String myModi(HttpSession session, Model model) {
		TmUserDTO member = memService.getMember((String)session.getAttribute("memId"));
		model.addAttribute("mem", member);
		return "tm/client/mypage/myModi";
	}
	
	//닉네임 수정 Form
	@RequestMapping("myModiNick.fl")
	public String myModiNick(TmUserDTO mem,Model model) {
		model.addAttribute("mem", mem);
		return "tm/client/mypage/myModiNickForm";
	}
	
	//닉네임 수정 시 중복 확인 
	@ResponseBody
	@RequestMapping("ajaxModiNick.fl")
	public String ajaxModiNick(@RequestBody Map<Object,Object> map) throws Exception {
		String result = "";
		String nickname = (String)map.get("nickname");
		System.out.println(nickname);
		//id와 newPw 넘겨주면 pw랑 맞는지 확인 !
		int check = memService.nickCheck(nickname);	//1이면 이미 존재, 0이면 존재 X
		System.out.println(check);
		if(check == 1) {
			result = "이미 존재하는 닉네임 입니다.";
		}else {
			result = "사용가능";
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(result);
		
		return  json;
	}
	
	//닉네임 수정 Pro
	//nickname 받아서 db에서 update하는 작업
	@RequestMapping("myModiNickPro.fl")
	public String myModiNickPro(TmUserDTO mem) {
		memService.updaNick(mem);
		//model.addAttribute("mem", mem);
		return "redirect:myModi.fl";
	}

	//로그인 일 때 비밀번호 재설정 Form
	@RequestMapping("myModiPwForm.fl")
	public String myModiPwForm(TmUserDTO mem,Model model) {
		model.addAttribute("mem", mem);
		
		return "tm/client/mypage/myModiPwForm";
	}
	
	//로그인 일 때 비밀번호 재설정 Pro
	@RequestMapping("myModiPwPro.fl")
	public String myModiPwPro(TmUserDTO mem) {
		System.out.println("mem.id"+mem.getId());
		System.out.println("mem.pw"+mem.getPw());
		memService.pwChange(mem);
		return "redirect:myModi.fl";
	}
	
	//회원 탈퇴 Form
	@RequestMapping("myDelete.fl")
	public String myDelete(String id,Model model) {
		model.addAttribute("id", id);
		return "tm/client/mypage/myDelete";
	}
	
	//회원 탈퇴 Pro
	//아이디, 비밀번호를 받아서 맞으면 삭제 아니면 돌아가라 
	@RequestMapping("myDeletePro.fl")
	public String myDeletePro(TmUserDTO mem) {
		int result = memService.idPwCheck(mem);
		if(result==1) {
			memService.changeStat(2, mem.getId());
		}
		
		return "tm/client/mypage/myDelete";
	}
	
	@ResponseBody
	@RequestMapping("ajaxIdCheck.fl")
	public String ajaxIdAvail(@RequestBody Map<Object,Object> map) throws Exception {
		String id = (String)map.get("id"); 
		String nickname = (String)map.get("nickname"); 
		String email = (String)map.get("email"); 
		Boolean idResult = true;
		Boolean nickResult = true;
		Boolean emailResult = true;
		//매개변수로 전달 받은 id가 DB에 존재하는지 확인
		int idCheck = memService.idCheck(id);	//1이면 이미 존재, 0이면 존재 X
		System.out.println("idcheck: " + idCheck);
		//닉네임이 중복 존재하는지 확인
		int nickCheck = memService.nickCheck(nickname);
		//email 중복 존재하는지 확인 
		int emailCheck =memService.eCheck(email);
		//id
		if(idCheck == 1) {
			idResult = false;
		}else {
			idResult = true;
		}
		//nickname
		if(nickCheck == 1) {
			nickResult = false;
		}else {
			nickResult = true;
		}
		//email
		if(emailCheck == 1) {
			emailResult = false;
		}else {
			emailResult = true;
		}
		Map mmap = new HashMap();
		mmap.put("idResult", idResult);
		mmap.put("nickResult", nickResult);
		mmap.put("emailResult", emailResult);
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(mmap);
		
		return  json;
	}
	
	//manual페이지
	@RequestMapping("manual.fl")
	public String manual() {
		
		return "tm/client/manual/manual";
	}
	
	//rk페이지
	@RequestMapping("rkDetail.fl")
	public String rank(Model model) {
		List<UserRkDTO> rk = memService.getRk();
		model.addAttribute("rkList",rk);
		return "tm/client/manual/rkDetail";
	}
	
	//position페이지
	@RequestMapping("positionDetail.fl")
	public String position(Model model) {
		List<SmallPosDTO> sPos = memService.getAllPos();
		model.addAttribute("sPos",sPos);
		return "tm/client/manual/positionDetail";
	}

	/* 	타 유저 이력 페이지 	*/
	
	//회원의 여행이력
	@RequestMapping("userHistory.fl")
	public String userHistory(String userId, Model model) throws Exception {
		//참여중인 여행 다 가져오기
		List<GroupSpaceDTO> travels = travelService.getMyGroups(userId, 1);
		//이력이므로 끝난 것만 보내기
		List<GroupSpaceDTO> travelAll = new ArrayList<GroupSpaceDTO>();
		for(int i=0 ; i<travels.size() ; i++) {
			if(travels.get(i).getStatus()==4) {
				travelAll.add(travels.get(i));
			}
		}
		model.addAttribute("travelAll", travelAll);
		model.addAttribute("userId",userId);
		return "tm/client/userpage/userHistory";
	}
	
	//회원의 코멘트
	@RequestMapping("userComment.fl")
	public String userInfo(String userId, Model model)throws SQLException {
		
	      
	      //코멘트에서 사용할 여행목록(코멘트 다 사라지면 목록에서 사라짐)
	      List cmtGroupList = commentService.cmtGroup(userId);
	      
	      //사용자가 여행한 모든 여행그룹인데 사용자 혼자여행인건 뺀 여행그룹 (상태4)
	      List<GroupSpaceDTO> cmtMyGroup = commentService.cmtMyGroup(userId);
	      //for(int i = 0; i<cmtMyGroup.size(); i++) {
	      // System.out.println("컨트롤러에서 dtoList" + ((GroupSpaceDTO)cmtMyGroup.get(i)).getSubject());
	      //}
	      
	      //그룹멤버들dto로 가져옴
	      List fin = commentService.groupUser(userId);
	      
	      // 여행 갯수 가져옴
	      int count = commentService.countGroup(userId);
	      
	      //받는사람이 사용자인 userCmtDTO가져오기
	      List comRecUser = commentService.comRecUser(userId);
	    
	      
	      //보낸사람이 사용자인 userCmtDTO가져오기
	      List comSenUser = commentService.comSenUser(userId);
	      
	      
	      // 그룹방별로 받는사람이 사용자인거 카운트
	      Map comRecUserCnt = commentService.comRecUserCnt(userId);
	      
	      // 그룹방별로 보낸사람이 사용자인거 카운트
	      Map comSenUserCnt = commentService.comSenUserCnt(userId);
	      
	      
	      //model.addAttribute("dtoList", dtoList);
	      
	      model.addAttribute("comSenUserCnt", comSenUserCnt);
	      model.addAttribute("comRecUserCnt", comRecUserCnt);
	      model.addAttribute("cmtMyGroup", cmtMyGroup);
	      model.addAttribute("cmtGroupList", cmtGroupList);
	      model.addAttribute("fin", fin);
	      model.addAttribute("count", count);
	      model.addAttribute("comRecUser", comRecUser);
	      model.addAttribute("comSenUser", comSenUser);

		model.addAttribute("userId",userId);
		return "tm/client/userpage/userComment";
	}
	
	//회원이 작성한 랜드마크
	@RequestMapping("userLand.fl")
	public String userLand(String userId, Model model) throws Exception {
		List writtenLand = landmarkService.myLand(userId);
		model.addAttribute("userId",userId);
		model.addAttribute("wLand",writtenLand);
		return "tm/client/userpage/userLand";
	}
	
	
	
	
	
	
	//카카오로그인 테스트
	@RequestMapping("kakaoTest.fl")
	public String kakaoTest() {
		return "/pf/shop/kakaoTest";
	}
	
	
	//카카오로그인테스트
	@ResponseBody
	@RequestMapping("kakaoTestPro.fl")
	public Map kakaoTestPro(@RequestBody Map<String,String> map) {
		if(map.get("gender").equals("female")) {
			map.put("gender", "1");
			System.out.println("맵젠더" + map.get("gender"));
		}else {
			System.out.println("문자열구분못한다");
		}
		
		memService.insertKakao(map);
		
		System.out.println("아이디"+map.get("id"));
		System.out.println(map.get("nickname"));
		System.out.println(map.get("email"));
		System.out.println(map.get("gender"));
		
		return map;
	}
	
	
	
}
