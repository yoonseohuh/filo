package portfolio.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import portfolio.model.dto.GameInfoDTO;
import portfolio.model.dto.GameRecordDTO;
import portfolio.model.dto.GrGiJoinDTO;
import portfolio.model.dto.WalletDTO;

@Repository
public class GameDAOImpl implements GameDAO  {

	@Autowired
	private SqlSessionTemplate sqlSession = null;
	
	
	//복권,룰렛 카운트 리셋
	@Override
	public void resetCnt() {
		sqlSession.update("game.resetCnt");
	}
	
	//회원가입 시 지갑 테이블에 레코드 insert
	@Override
	public void addWallet(String id) {
		sqlSession.insert("game.addWallet",id);
	}
	
	//회원 wallet 리턴
	@Override
	public WalletDTO getWallet(String id) {
		WalletDTO wallet = sqlSession.selectOne("game.userWallet",id);
		return wallet;
	}
	
	//회원의 모든 gameRecord 리턴
	@Override
	public List<GrGiJoinDTO> getGameRecord(String id) {
		List<GrGiJoinDTO> records = sqlSession.selectList("game.userAllRecord",id);
		return records;
	}
	
	//게임 정보 리턴
	@Override
	public GameInfoDTO getGameInfo(int gameCate) {
		GameInfoDTO info = sqlSession.selectOne("game.getGameInfo",gameCate);
		return info;
	}
	
	//게임 레코드 insert
	@Override
	public void insertRecord(Map map) {
		sqlSession.insert("game.insertRecord",map);
	}
	
	//지갑에 포인트 업데이트
	@Override
	public void updatePoint(Map map) {
	/*	1) 서비스에서 updatePoint() 메서드를 탄 경우라면 map에는 id와 needPoint가 있음
		2) 서비스에서 insRecordPoint() 메서드를 탄 경우라면 map에는 id, gameCate, score가 있음	*/
		sqlSession.update("game.updatePoint",map);
	}
	
	//데일리 룰렛 횟수 올리기
	@Override
	public void updateWheelCnt(String user) {
		sqlSession.update("game.updateWheelCnt",user);
	}
	
	//복권 구매 횟수 올리기
	@Override
	public void updateLotteryCnt(String user) {
		sqlSession.update("game.updateLotteryCnt",user);
	}
	
	//상위 퍼센트 정보 구하기
	@Override
	public Map gamePercent(String user) {
		System.out.println("게임퍼센트");
		double myRank = sqlSession.selectOne("game.myRank",user);
		double allCnt = sqlSession.selectOne("game.allMemCnt", user);
		
		Map map = new HashMap<Double, Double>();
		map.put("myRank", myRank);
		map.put("allCnt", allCnt);
		
		return map;
	}
	
	//랭킹 세 명 정보
	@Override
	public List topThree() {
		
		return null;
	}
	
	//게임 해봤는지 여부
	@Override
	public int haveEverPlayed(String user) {
		int check = sqlSession.selectOne("game.haveEverPlayed",user);
		return check;
	}
	
}
