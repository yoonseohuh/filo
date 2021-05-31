package portfolio.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import portfolio.model.dto.IconDTO;

@Repository
public class ShopDAOImpl implements ShopDAO {
	
	@Autowired
	private SqlSessionTemplate sqlSession = null;

	@Override
	public List<IconDTO> getIcon() {
		System.out.println("다오");
		List getIcon = sqlSession.selectList("game.getIcon");
		System.out.println("다오나옴");
		return getIcon;
	}

}