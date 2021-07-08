package com.kh.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.kh.dto.AnimalFilesDTO;
import com.kh.dto.FileDTO;

public class FileDAO {
private static FileDAO instance;
	
	public synchronized static FileDAO getInstance() {
		if(instance == null) {
			instance = new FileDAO();
		}
		return instance;
	}
	
	private FileDAO() {}
	
	private Connection getConnection() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/oracle");
		return ds.getConnection();
	}
	
	// animal
	public int animalImgUpload(AnimalFilesDTO fdto) throws Exception {
		String sql = "insert into animal_photos values(photo_seq.nextval, ?, ?, sysdate, ?)";
		try (
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){

			pstat.setString(1, fdto.getOri_name());
			pstat.setString(2, fdto.getSys_name());
			pstat.setString(3, fdto.getCode_seq());

			int result = pstat.executeUpdate();
			return result;
		}
	}
	
	// animal
	public  AnimalFilesDTO getAnimalImgs(String parent) throws Exception{
		String sql = "select * from animal_photos where code_seq = ?";
		try (
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);

				){
			pstat.setString(1, parent);

			try(
					ResultSet rs = pstat.executeQuery();
					){
				if(rs.next()) {
					AnimalFilesDTO dto = new AnimalFilesDTO();

					dto.setAnfile_seq(rs.getInt("photo_seq"));
					dto.setOri_name(rs.getNString("ori_name"));
					dto.setSys_name(rs.getNString("sys_name"));
					dto.setReg_date(rs.getDate("reg_date"));
					dto.setCode_seq(parent);

					return dto;
				}
				return null;
			}
		}
	}

	// animal
	public  List<AnimalFilesDTO> selectAll(String parent) throws Exception{
		String sql = "select * from animal_photos where code_seq = ?";
		try (
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);

				){
			pstat.setString(1, parent);
			try(
					ResultSet rs = pstat.executeQuery();
					){
				List<AnimalFilesDTO> list = new ArrayList<AnimalFilesDTO>();
				while(rs.next()) {
					int seq = rs.getInt("photo_seq");
					String oriName = rs.getNString("ori_name");
					String sysName = rs.getNString("sys_name");
					Date reg_date = rs.getDate("reg_date");

					list.add(new AnimalFilesDTO(seq, oriName, sysName, reg_date, parent));
				}
				return list;
			}

		}

	}
	
	// notice
	public String getSysName(int seq) throws Exception {
		String sql = "select sysname from files where seq = ?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			pstat.setInt(1, seq);
			try(ResultSet rs = pstat.executeQuery()){
				rs.next();
				return rs.getString("sysname");
			}
		}
	}
	
	// notice
	public int insert(FileDTO dto) throws Exception{
		String sql = "insert into files values(files_seq.nextval,?,?,sysdate,?)";
		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			
				pstat.setString(1, dto.getOriName());
				pstat.setString(2, dto.getSysName());
				pstat.setInt(3, dto.getParent());
				int result = pstat.executeUpdate();
				con.commit();
				return result;
		}
	}
	
	// notice
	public List<FileDTO> selectBySeq(int fparent) throws Exception {
		String sql = "select * from files where parent = ?";
		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);				
				){
			pstat.setInt(1, fparent);
			try(ResultSet rs = pstat.executeQuery()
					){
				List<FileDTO> list = new ArrayList<>();
				while(rs.next()) {
					int seq = rs.getInt("seq");
					String oriName = rs.getString("oriName");
					String sysName = rs.getString("sysName");
					Date reg_date = rs.getDate("reg_date");
					int parent = rs.getInt("parent");
					list.add(new FileDTO(seq,oriName,sysName,reg_date,parent));
					
				}
				return list;
			}
			
		}
		
	}
	
	// notice
	public int delete(int seq) throws Exception {
		String sql = "delete from files where seq = ?";
	
		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
				pstat.setInt(1, seq);
				int result = pstat.executeUpdate();
				con.commit();
				return result;			
		}	
	}

}
