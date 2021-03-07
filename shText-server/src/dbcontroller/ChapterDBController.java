package dbcontroller;

import buffer.ChapterBuffer;
import buffer.ChapterContentBuffer;
import dbcontroller.rowmapper.ChapterMapper;
import domain.Book;
import domain.Chapter;
import domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import util.Text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ChapterDBController {
    private JdbcTemplate jt;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jt = jdbcTemplate;
    }


    public List<Chapter> queryAllChapter(){
        String sql="select cid,ctitle,cseq,ccharNum,bid from chapter";
        try{
            List<Chapter> chapter=jt.query(sql,new ChapterMapper());
            return chapter;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public Chapter queryChapterByCid(int cid){
        String sql="select cid,ctitle,cseq,ccharNum,bid from chapter where cid="+cid;
        try{
            Chapter c=jt.queryForObject(sql,new ChapterMapper());
            return c;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<Chapter> queryChapterByBook(int bid){
        String sql="select cid,ctitle,cseq,ccharNum,bid from chapter where chapter.bid="+Integer.toString(bid)+" order by cseq";
        try{
            List<Chapter> c=jt.query(sql,new ChapterMapper());
            return c;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public Integer queryBidByCid(int cid){
        String sql="select bid from chapter where cid="+cid;
        try{
            int bid=jt.queryForObject(sql,new RowMapper<Integer>(){
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt("bid");
                }
            });
            return bid;
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public Integer queryChapterCount(){
        String sql="select count(*) from chapter";
        try{
            int i=jt.queryForObject(sql, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt(1);
                }
            });
            return i;
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //获取内容缓存
    public ChapterContentBuffer queryChapterContent(int cid){
        if(ChapterBuffer.getChapterBuffer().getChapterContentByCid(cid)!=null)
            return ChapterBuffer.getChapterBuffer().getChapterContentByCid(cid);
        else {
            String sql="select cid,ccontent,ctitle,cseq,bid from chapter where cid="+cid;
            try{
                ChapterContentBuffer m=jt.queryForObject(sql, new RowMapper<ChapterContentBuffer>() {
                    @Override
                    public ChapterContentBuffer mapRow(ResultSet resultSet, int i) throws SQLException {
                        ChapterContentBuffer ccb = new ChapterContentBuffer(resultSet.getString( "ccontent"), cid);
                        ccb.setCtitle(resultSet.getString("ctitle"));
                        ccb.setCseq(resultSet.getInt("cseq"));
                        ccb.setBid(resultSet.getInt("bid"));
                        ChapterBuffer.getChapterBuffer().addChapterContent(cid, ccb);
                        return ccb;
                    }
                });
                return m;
            }catch(EmptyResultDataAccessException e){
                return null;
            }
        }
    }

    //更新信息
    public boolean updateChapterInfo(Chapter chapter){
        String sql="update chapter set ctitle=?,cseq=?,bid=? where cid=?";
        Object obj[]=new Object[]{chapter.getCtitle(),chapter.getCseq(),chapter.getBid(),chapter.getCid()};
        return jt.update(sql,obj)==1;
    }

    //更新内容
    public boolean updateChapterContent(ChapterContentBuffer ccb){
        String sql="update chapter set ccontent=?,ccharNum=? where cid=?";
        PreparedStatementSetter ps=new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                String temp=ccb.getContent();
                preparedStatement.setString(1,temp);
                preparedStatement.setInt(2,temp.length());
                preparedStatement.setInt(3,ccb.getCid());
            }
        };
        return jt.update(sql,ps)==1;
    }

    public boolean insertChapter(Chapter chapter){
        String sql="insert into chapter(cid,ctitle,cseq,ccharNum,ccontent,bid) values(?,?,?,0,'',?)";
        Object obj[]=new Object[]{chapter.getCid(),chapter.getCtitle(),chapter.getCseq(),chapter.getBid()};
        return jt.update(sql,obj)==1;
    }

    public boolean deleteChapter(int cid){
        String sql="delete from chapter where cid=?";
        Object obj[]=new Object[]{cid};
        return jt.update(sql,obj)==1;
    }

}
