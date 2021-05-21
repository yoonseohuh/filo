package portfolio.controller.bean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import portfolio.model.dto.GameInfoDTO;
import portfolio.service.bean.GameService;

@Controller
@RequestMapping("/game/")
public class GameController {
	
	@Autowired
	private GameService gameService = null;
	
	@Scheduled(cron = "1 0 0 * * *")
	public void resetCnt() {
		System.out.println("매일 0시 0분 1초 실행 ["+new Date()+"]");
		gameService.resetCnt();
	}
	
	
	int answer = 0;	//updown 시 ajax와 게임페이지에서 변수를 공유하기 위해 바깥으로 뺌
	
	//hys up-down game page
	@RequestMapping("updown.fl")
	public String numUpDown(Model model) {
		String user = (String)RequestContextHolder.getRequestAttributes().getAttribute("memId", RequestAttributes.SCOPE_SESSION);
		answer = (int)(Math.random()*25)+1;	//0~24 +1
		if(user!=null) {
			int needPoint = gameService.getGameInfo(1).getNeedPoint();
			int userPoint = gameService.getWallet(user).getPoint();
			boolean isPossible = false;
			if(needPoint>userPoint) {
				isPossible = false;	//게임 불가능
			}else {
				isPossible = true;	//게임 가능
				gameService.updatePoint(user,-needPoint);	//포인트 차감
			}
			model.addAttribute("isPossible",isPossible);
			model.addAttribute("answer",answer);
		}
		
		return "/pf/game/numUpDown";
	}
	
	//hys up-down game ajax
	@ResponseBody
	@RequestMapping("updownGuess.fl")
	public Map numUpDownGuess(@RequestBody Map<String, Integer> map) throws Exception {
		String user = (String)RequestContextHolder.getRequestAttributes().getAttribute("memId", RequestAttributes.SCOPE_SESSION);
		
		int chance = map.get("chance");
		int input = map.get("guess");
		int start = map.get("start");
		int end = map.get("end");
		//승리시 1, 패배시 0, 게임 진행 중에는 -1
		int result = -1;
		if(chance>1) {
			if(input==answer) {
				result = 1;
				System.out.println("정답! user:"+user+"/result:"+result);
				gameService.insRecordPoint(user,1,50);	//이기면 50포인트
			}else if(input>answer) {
				chance-=1;
				end = input-1;
			}else if(input<answer) {
				chance-=1;
				start = input+1;
			}
		}else if(chance<=1){
			chance-=1;
			if(input==answer) {
				result = 1;
				System.out.println("정답! user:"+user+"/result:"+result);
				gameService.insRecordPoint(user,1,50);	//이기면 50포인트
			}else {
				result = 0;
				System.out.println("실패! user:"+user+"/result:"+result);
				gameService.insRecordPoint(user,1,0);	//지면 0포인트
			}
		}
		map.put("input",input);
		map.put("chance",chance);
		map.put("start",start);
		map.put("end",end);
		map.put("result",result);
		return map;
	}
	
	//lsh rockPS game page
	@RequestMapping("rockPS.fl")
	public String rockPS() {
		
		return "/pf/game/rockPS";
	}
	
	//lhs ajaxTest
	@ResponseBody
	@RequestMapping("insertRockResult")
	public String insertRockResult(@RequestBody Map<Object, Object> map)throws Exception {
		String result ="";
		String user = (String)RequestContextHolder.getRequestAttributes().getAttribute("memId", RequestAttributes.SCOPE_SESSION);

		int pScore = (int)map.get("pScoreResult");
		int cScore = (int)map.get("cScoreResult");
		System.out.println(pScore);
		System.out.println(cScore);
		
		//게임 고유 번호, 아이디, 종목, 승패여부
		//1. 스코어를 넣었을 때 승패 여부 알려주기
		//승리시 1 패배시 0 
		int gameResult = gameService.rockResult(pScore,cScore);
		System.out.println("gameResult:"+gameResult);
		//2. 아이디, 승패여부, 종목 db에 넣어주기 
		if(gameResult==1) {
			gameService.insRecordPoint(user, 2, 60);
		}else if(gameResult==0) {
			gameService.insRecordPoint(user, 2, 0);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(result);
		return json;
	}
	
	//lsm lottery game page
	@RequestMapping("lottery.fl")
	public String lottery() {
		
		return "pf/game/lottery";
	}
	
	
	//jbr
	@RequestMapping("randomPick.fl")
	public String randomPick(Model model) {
		String result = gameService.randomNum();
		model.addAttribute("result", result);
		return "pf/game/randomPick";
	}
	
	//jbr
	@RequestMapping("oneToFifty")
	public String oneToFifty(Model model) {
		int[] numArray = gameService.oneToFifty();
		
		model.addAttribute("numArray", numArray);
		return "pf/game/1to50";
	}
	
	
	//jbr
	@RequestMapping("card.fl")
	public String card() {
		
		return "pf/game/card";
	}
	
	//룰렛
	@RequestMapping("winwheel.fl")
	public String winwheel(){
		
		return "pf/game/winwheel";
	}
	
	//룰렛 ajax
	@ResponseBody
	@RequestMapping("winwheelAjax.fl")
	public void winwheelAjax(String pointText){
		System.out.println("포인트" + pointText);
		
		String point[] = pointText.split("p");
		System.out.println("잘 잘렸나" + point[0]);
		int p = Integer.parseInt(point[0]);
		System.out.println("숫자변환" + p);
		//인트로변환해줘야함
		
		String user = (String)RequestContextHolder.getRequestAttributes().getAttribute("memId", RequestAttributes.SCOPE_SESSION);
		// gameService.updatePoint(user, p);
		// gameService.updateLottety(user);
		
		
	}
}
