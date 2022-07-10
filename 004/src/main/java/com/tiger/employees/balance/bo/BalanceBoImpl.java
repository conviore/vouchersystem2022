package com.tiger.employees.balance.bo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.tiger.employees.balance.dao.BalanceDao;
import com.tiger.employees.balance.po.Balance;
import com.tiger.employees.monsettleprog.dao.MonthSettleProgressDao;
import com.tiger.employees.monsettleprog.po.MonthSettleProgress;
import com.tiger.employees.subject.dao.SubjectDao;
import com.tiger.employees.subject.po.Subject;
import com.tiger.utilities.SettleConfig;
import com.tiger.utilities.TimeUtil;



public class BalanceBoImpl implements BalanceBo{
	
	@Autowired
	BalanceDao balanceDao;
	
	@Autowired
	SubjectDao subjectDao;
	
	@Autowired
	MonthSettleProgressDao monthSettleProgressDao;
	
	private final static String subject_root_id="1";
	
	private List resultList_1=new ArrayList();
	private List resultList_2=new ArrayList();
	private List resultList_3=new ArrayList();
	private List resultList_4=new ArrayList();
	private List resultList_5=new ArrayList();

	public void saveBalance(Balance balance) {
		// TODO Auto-generated method stub
		this.balanceDao.addBalance(balance);
		
	}

	public Balance getBalanceById(String id) {
		// TODO Auto-generated method stub
		Balance balance=this.balanceDao.getBalance(id);
		return balance;
		
	}

	public List<Balance> getBalances(Map param) {
		// TODO Auto-generated method stub
		List resultList=null;
		resultList=this.balanceDao.getListBalance(param);
		return resultList;
	}

	public void saveListBalances(List<Balance> balance) {
		// TODO Auto-generated method stub
		if(balance!=null){
			this.balanceDao.addListBalance(balance);
		}
	}

	public List<Balance> generateSubjectSummary(Map param) {
		// TODO Auto-generated method stub
		List resultList=new ArrayList();
		String settleTime=(String) param.get("settleTime");
		String dateYear=settleTime.substring(0,4);
         String dateMonth=settleTime.substring(4, 6);
         String dateDay=null;
         if(dateMonth.equals("01")||dateMonth.equals("03")||dateMonth.equals("05")||dateMonth.equals("07")||dateMonth.equals("08")||dateMonth.equals("10")||dateMonth.equals("12")){
        	 dateDay="31";
         }else if(dateMonth.equals("02")){
        	 dateDay="28";
         }else{
        	 dateDay="30";
         }
 		System.out.println("");
 		System.out.println("month is "+dateMonth);
 		System.out.println("year is "+dateYear);
 		
 		String oldSettleDateMonth=null;
 		String oldSettleDateYear=dateYear;
 		String oldDateDay=null;
 		Integer monthInt=null;
 		
 		settleTime=settleTime+dateDay;
 		
 		if(dateMonth.equals("01")){
 			oldSettleDateMonth="12";
 			oldSettleDateYear=String.valueOf(Integer.valueOf(dateYear)-1);
 		}
 		else{
 			monthInt=Integer.valueOf(dateMonth)-1;
 			oldSettleDateMonth=String.valueOf(monthInt);
 			if(monthInt<10){
 				oldSettleDateMonth="0"+String.valueOf(monthInt);
 			} 			
 		}
 		
 		String oldSettleDay=null;
        if(oldSettleDateMonth.equals("01")||oldSettleDateMonth.equals("03")||oldSettleDateMonth.equals("05")||oldSettleDateMonth.equals("07")||oldSettleDateMonth.equals("08")||oldSettleDateMonth.equals("10")||oldSettleDateMonth.equals("12")){
        	oldSettleDay="31";
        }else if(oldSettleDateMonth.equals("02")){
        	oldSettleDay="28";
        }else{
        	oldSettleDay="30";
        }
		
 		String oldSettleDate=oldSettleDateYear+oldSettleDateMonth+oldSettleDay;
		
		String companyId=(String)param.get("companyId");
		//resultList=this.generateSummary(companyId,settleTime,oldSettleDate, subject_root_id,resultList);
		resultList=this.generateSummary_MutiWorker(companyId,settleTime,oldSettleDate, subject_root_id,resultList);
		return resultList;
	}
	
	private List<Balance> generateSummary_MutiWorker(String companyId,String settleTime,String oldSettleDate,String parentId,List resultList){
		
		resultList_1.clear();
		resultList_2.clear();
		resultList_3.clear();
		resultList_4.clear();
		resultList_5.clear();
		
		Worker1Thread w1=new Worker1Thread(companyId,settleTime,oldSettleDate,parentId);
		w1.start();
		Worker2Thread w2=new Worker2Thread(companyId,settleTime,oldSettleDate,parentId);
		w2.start();
		Worker3Thread w3=new Worker3Thread(companyId,settleTime,oldSettleDate,parentId);
		w3.start();
		Worker4Thread w4=new Worker4Thread(companyId,settleTime,oldSettleDate,parentId);
		w4.start();
		Worker5Thread w5=new Worker5Thread(companyId,settleTime,oldSettleDate,parentId);
		w5.start();
		
		while(w1.getResult_code1().size()==0||resultList_2.size()==0||resultList_3.size()==0||resultList_4.size()==0||resultList_5.size()==0) {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("Waitting");
		}
		resultList.addAll(w1.getResult_code1());
		resultList.addAll(resultList_2);
		resultList.addAll(resultList_3);
		resultList.addAll(resultList_4);
		resultList.addAll(resultList_5);
		
		
		w1.interrupt();
		w2.interrupt();
		w3.interrupt();
		w4.interrupt();
		w5.interrupt();
		
		
		
		return resultList;
	}
	
	private List<Balance> generateSummary(String companyId,String settleTime,String oldSettleDate,String parentId,List resultList){
		Map param=new HashMap();
		Map balanceParam=new HashMap();
		List<Balance> balanceList=new ArrayList();
		param.put("parentId", parentId);
		param.put("orgId", companyId);
		List<Subject> subjectList=this.subjectDao.querySubjects(param);
		String subId=null;
		String subCode=null;
		Integer accountDirection=1;
		if(subjectList!=null){
			for(Subject sub:subjectList){
				subId=sub.getId();
				subCode=sub.getCode();
				accountDirection=sub.getAccountDirection();
				balanceParam.put("subjectId", subId);
				balanceParam.put("orgId", companyId);
				balanceParam.put("settleTime", oldSettleDate);
				balanceList=this.balanceDao.getListBalance(balanceParam);
				Balance oldPo=null;
				//if this is a new subject, then the old po must be null and we insert into all zero
				if(balanceList.isEmpty()||balanceList==null||balanceList.get(0)==null){
					 oldPo=new Balance();
					 oldPo.setFinalMonCredit(new BigDecimal("0"));
					 oldPo.setFinalMonDebit(new BigDecimal("0"));
					 oldPo.setCumulativeDebit(new BigDecimal("0"));
					 oldPo.setCumulativeCredit(new BigDecimal("0"));
					 oldPo.setSettleTime(oldSettleDate);
				}
				else{
					 oldPo=balanceList.get(0);					
				}
				
				BigDecimal initMonCredit=oldPo.getFinalMonCredit();
				BigDecimal initMonDebit=oldPo.getFinalMonDebit();
				BigDecimal cumulativeCredit=oldPo.getCumulativeCredit();
				BigDecimal cumulativeDebit=oldPo.getCumulativeDebit();
				//骞村垵 鏈勾鍙戠敓棰濋噸缃�
				if(oldPo.getSettleTime().substring(4, 6).equals("12")){
					cumulativeCredit=new BigDecimal("0");
					cumulativeDebit=new BigDecimal("0");
				}
				Balance newBalance=new Balance();
				newBalance.setUpdateTime(TimeUtil.getCurrentTime17ByMillis());
				newBalance.setSubjectId(subId);
				newBalance.setSubjectName(sub.getComment());
				newBalance.setSubjectCode(sub.getCode());
				newBalance.setInitMonCredit(initMonCredit);
				newBalance.setInitMonDebit(initMonDebit);
				newBalance.setSettleTime(settleTime);
				newBalance.setOrgId(companyId);
				
				if(sub.getIsLeaf().equals("0")){
					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt,subject s where vt.subject_id=s.id and vt.settle_time like '"+settleTime.substring(0,6)+"%' and vt.org_id='"+companyId+"' and s.code like '"+subCode+"%'";
					List sumResultL=this.balanceDao.execSql(sql);
					Object[] sumAarry=(Object[]) sumResultL.get(0);
					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
					
					newBalance.setCumulativeCredit(newCumulativeCredit);
					newBalance.setCumulativeDebit(newCumulativeDebit);
					newBalance.setCurrentMonCredit(sumCredit);
					newBalance.setCurrentMonDebit(sumDebit);
					newBalance.setFinalMonCredit(finalMonCredit);
					newBalance.setFinalMonDebit(finalMonDebit);
					
					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
					newBalance.setAccountDirection(sub.getAccountDirection());
					
					//鏌ュ嚭骞村垵浣欓
					if(!settleTime.substring(4, 6).equals("01")){
						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
						List initBalanceResultL=this.balanceDao.execSql(queryInitYearSql);
						if(initBalanceResultL.isEmpty()){
							newBalance.setInitYearBalance(new BigDecimal("0"));
						}
						else{
//							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//							BigDecimal initValue=(BigDecimal) initAarry[0];
							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
							newBalance.setInitYearBalance(initValue);
						}
					}
					else{
						newBalance.setInitYearBalance(newBalance.getInitBalance());
					}
					
					resultList.add(newBalance);
					resultList=this.generateSummary(companyId, settleTime, oldSettleDate, subId, resultList);
				}
				else{
					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt where vt.settle_time like '"+settleTime.substring(0,6)+"%'  and vt.org_id='"+companyId+"' and vt.subject_id='"+subId+"'";
					List sumResultL=this.balanceDao.execSql(sql);
					Object[] sumAarry=(Object[]) sumResultL.get(0);
					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
					
					newBalance.setCumulativeCredit(newCumulativeCredit);
					newBalance.setCumulativeDebit(newCumulativeDebit);
					newBalance.setCurrentMonCredit(sumCredit);
					newBalance.setCurrentMonDebit(sumDebit);
					newBalance.setFinalMonCredit(finalMonCredit);
					newBalance.setFinalMonDebit(finalMonDebit);
					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
					newBalance.setAccountDirection(sub.getAccountDirection());
					//鏌ュ嚭骞村垵浣欓
					if(!settleTime.substring(4, 6).equals("01")){
						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
						List initBalanceResultL=this.balanceDao.execSql(queryInitYearSql);
						if(initBalanceResultL.isEmpty()){
							newBalance.setInitYearBalance(new BigDecimal("0"));
						}
						else{
//							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//							BigDecimal initValue=(BigDecimal) initAarry[0];
							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
							newBalance.setInitYearBalance(initValue);
						}
					}
					else{
						newBalance.setInitYearBalance(newBalance.getInitBalance());
					}
					
					resultList.add(newBalance);
				}
				
				
				
				
				
			}
		}
		
		
		
		return resultList;
	}

	public InputStream getBalanceExcel(Map param) {
		// TODO Auto-generated method stub
		if(param!=null){
			//鑾峰緱鎶ヨ〃鏁版嵁
			String companyName=(String) param.get("companyName");
			param.remove("companyName");
			List<Balance> balanceList=this.generateSubjectSummary(param);
			String settleDate=(String) param.get("settleTime");
			
			/**
			 * 鍒朵綔鎶ヨ〃
			 */
			HSSFWorkbook workbook = new HSSFWorkbook();  
	        HSSFSheet sheet = workbook.createSheet("sheet1");  
	        {  
	            // 鍒涘缓琛ㄥご  
	            HSSFRow row = sheet.createRow(0);  
	            HSSFCell cell = row.createCell((short) 0);  
	            cell.setCellValue(companyName);  
	            cell = row.createCell((short) 1);  
	            cell.setCellValue("绉戠洰浣欓姹囨��");  
	            cell = row.createCell((short) 2);  
	            cell.setCellValue("浼氳鏈熼棿");  
	            cell = row.createCell((short) 3);  
	            cell.setCellValue(settleDate);  
	            
	            //鍒涘缓鍒楀ご
	            row = sheet.createRow(1);  
	            cell = row.createCell((short) 0);  
	            cell.setCellValue("浼氳鏈熼棿");  
	            cell = row.createCell((short) 1);  
	            cell.setCellValue("绉戠洰缂栫爜");  
	            cell = row.createCell((short) 2);  
	            cell.setCellValue("绉戠洰鍚嶇О");  
	            cell = row.createCell((short) 3);
	            cell.setCellValue("骞村垵浣欓");
	            cell = row.createCell((short) 4);
	            cell.setCellValue("鏈堝垵浣欓");
	            cell = row.createCell((short) 5);
	            cell.setCellValue("鏈熷垵浣欓");
	            cell = row.createCell((short) 6);
	            cell.setCellValue("鏈熷垵鍊熸柟");
	            cell = row.createCell((short) 7);  
	            cell.setCellValue("鏈熷垵璐锋柟");
	            cell = row.createCell((short) 8);  
	            cell.setCellValue("鏈湡鍙戠敓鍊熸柟");
	            cell = row.createCell((short) 9);
	            cell.setCellValue("鏈湡鍙戠敓璐锋柟");
	            cell = row.createCell((short) 10);
	            cell.setCellValue("绱鍙戠敓鍊熸柟");
	            cell = row.createCell((short) 11);
	            cell.setCellValue("绱鍙戠敓璐锋柟");
	            cell = row.createCell((short) 12);
	            cell.setCellValue("鏈熸湯鍊熸柟");
	            cell = row.createCell((short) 13);
	            cell.setCellValue("鏈熸湯璐锋柟");
	            cell = row.createCell((short) 14);
	            cell.setCellValue("鏈熸湯浣欓");

	            // 鍒涘缓鏁版嵁  
	            // 绗竴琛�  鍏徃 浼氳鏈熼棿
	            Balance balance=null;
	            for(int i=0;i<balanceList.size();i++){
	            	row = sheet.createRow(i+2);
	            	balance=balanceList.get(i);
	            	cell = row.createCell((short) 0);  
//		            cell.setCellValue("浼氳鏈熼棿"); 
	            	cell.setCellValue(balance.getSettleTime());
		            cell = row.createCell((short) 1);  
//		            cell.setCellValue("绉戠洰缂栫爜");
		            cell.setCellValue(balance.getSubjectCode());
		            cell = row.createCell((short) 2);  
//		            cell.setCellValue("绉戠洰鍚嶇О");  
		            cell.setCellValue(balance.getSubjectName());
		            cell = row.createCell((short) 3);  
//		            cell.setCellValue("骞村垵浣欓");
		           
		            cell.setCellValue(balance.getInitYearBalance().doubleValue());
		            cell = row.createCell((short) 4);  
//		            cell.setCellValue("鏈堝垵浣欓");
		           
		            cell.setCellValue(balance.getInitBalance().doubleValue());
		            cell = row.createCell((short) 5);  
//		            cell.setCellValue("鏈熷垵浣欓");
		           
		            cell.setCellValue(balance.getInitBalance().doubleValue());
		            cell = row.createCell((short) 6);
//		            cell.setCellValue("鏈熷垵鍊熸柟");
		            cell.setCellValue(balance.getInitMonDebit().doubleValue());
		            cell = row.createCell((short) 7);  
//		            cell.setCellValue("鏈熷垵璐锋柟");
		            cell.setCellValue(balance.getInitMonCredit().doubleValue());
		            cell = row.createCell((short) 8);  
//		            cell.setCellValue("鏈湡鍙戠敓鍊熸柟");
		            cell.setCellValue(balance.getCurrentMonDebit().doubleValue());
		            cell = row.createCell((short) 9);
//		            cell.setCellValue("鏈湡鍙戠敓璐锋柟");
		            cell.setCellValue(balance.getCurrentMonCredit().doubleValue());
		            cell = row.createCell((short) 10);
//		            cell.setCellValue("绱鍙戠敓鍊熸柟");
		            cell.setCellValue(balance.getCumulativeDebit().doubleValue());
		            cell = row.createCell((short) 11);
//		            cell.setCellValue("绱鍙戠敓璐锋柟");
		            cell.setCellValue(balance.getCumulativeCredit().doubleValue());
		            cell = row.createCell((short) 12);
//		            cell.setCellValue("鏈熸湯鍊熸柟");
		            cell.setCellValue(balance.getFinalMonDebit().doubleValue());
		            cell = row.createCell((short) 13);
//		            cell.setCellValue("鏈熸湯璐锋柟");
		            cell.setCellValue(balance.getFinalMonCredit().doubleValue());
		            cell = row.createCell((short) 14);
//		            cell.setCellValue("鏈熸湯浣欓");
		            cell.setCellValue(balance.getEndBalance().doubleValue());
	            }
	        }  
	 
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        try {  
	            workbook.write(baos);  
	            baos.flush();
	            baos.close();
	            byte[] ba = baos.toByteArray();  
	            ByteArrayInputStream bais = new ByteArrayInputStream(ba);  
	            return bais;  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	            return null;
	        }  
		}
		return null;
	}

	public void endMonSettle(Map param) {
		// TODO Auto-generated method stub
		
		String settleTime=(String) param.get("settleTime");
		String companyId=(String)param.get("companyId");
		String dateYear=settleTime.substring(0,4);
        String dateMonth=settleTime.substring(4, 6);
        String monProgTime=dateYear+""+dateMonth;
		
        //鍒犻櫎鏃ф暟鎹�
        String delSql="delete from balance where settle_time >'"+settleTime+"00' and settle_time<'"+settleTime+"99' and org_id='"+companyId+"'";
        this.monthSettleProgressDao.execDmlSql(delSql);
        
		//鐢熸垚淇濆瓨鏁版嵁
		List<Balance> monEndBalanceList=this.generateSubjectSummary(param);
		
		//淇濆瓨l
		this.saveListBalances(monEndBalanceList);
		
		//鏇存柊鏈堢粨鐘舵�佽〃
		Map mspMap=new HashMap();
		mspMap.put("settleYear", dateYear);
		mspMap.put("settleMonth", dateMonth);
		mspMap.put("companyId", companyId);
		List<MonthSettleProgress> mspList=this.monthSettleProgressDao.getMSPs(mspMap);
		MonthSettleProgress msp=mspList.get(0);
		if(msp!=null){
			msp.setStatus(SettleConfig.MON_SETTLE_COMPLETE);
			msp.setComment("鏈堢粨瀹屾垚");
			msp.setUpdateTime(TimeUtil.getCurrentTime17ByMillis());
		}
		
		this.monthSettleProgressDao.updateMSP(msp);
		
		String createTime=msp.getCreateTime();
		String updatesql="update month_settle_progress set comment='鏈畬鎴�',status='"+SettleConfig.MON_SETTLE_INCOMPLETE+"' where company_id='"+companyId+"' and create_time > '"+createTime+"'";
		this.monthSettleProgressDao.execDmlSql(updatesql);
		
		System.out.println("end msp finished");
		
		
		
		
		
	}
	
    class Worker1Thread extends Thread{
        
    	String companyId;
        String settleTime;
        String oldSettleDate;
        String parentId;
        List  result_code1;
    	
        
    	public Worker1Thread(String companyId,String settleTime,String oldSettleDate,String parentId) {
        	 this.companyId=companyId;
        	 this.settleTime=settleTime;
        	 this.oldSettleDate=oldSettleDate;
        	 this.parentId=parentId;
        	 this.result_code1=new ArrayList();

    	}
    	
        public void run() {
            // TODO Auto-generated method stub
        	
        	List resultList = new ArrayList(); 
        	Map param=new HashMap();
    		Map balanceParam=new HashMap();
    		List<Balance> balanceList=new ArrayList();
    		param.put("parentId", parentId);
    		param.put("orgId", companyId);
    		param.put("code", "1");
    		List<Subject> subjectList=subjectDao.queryCodeLikeSubjects(param);
    		String subId=null;
    		String subCode=null;
    		Integer accountDirection=1;
    		if(subjectList!=null){
    			for(Subject sub:subjectList){
    				subId=sub.getId();
    				subCode=sub.getCode();
    				accountDirection=sub.getAccountDirection();
    				balanceParam.put("subjectId", subId);
    				balanceParam.put("orgId", companyId);
    				balanceParam.put("settleTime", oldSettleDate);
    				balanceList=balanceDao.getListBalance(balanceParam);
    				Balance oldPo=null;
    				//if this is a new subject, then the old po must be null and we insert into all zero
    				if(balanceList.isEmpty()||balanceList==null||balanceList.get(0)==null){
    					 oldPo=new Balance();
    					 oldPo.setFinalMonCredit(new BigDecimal("0"));
    					 oldPo.setFinalMonDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeCredit(new BigDecimal("0"));
    					 oldPo.setSettleTime(oldSettleDate);
    				}
    				else{
    					 oldPo=balanceList.get(0);					
    				}
    				
    				BigDecimal initMonCredit=oldPo.getFinalMonCredit();
    				BigDecimal initMonDebit=oldPo.getFinalMonDebit();
    				BigDecimal cumulativeCredit=oldPo.getCumulativeCredit();
    				BigDecimal cumulativeDebit=oldPo.getCumulativeDebit();
    				//骞村垵 鏈勾鍙戠敓棰濋噸缃�
    				if(oldPo.getSettleTime().substring(4, 6).equals("12")){
    					cumulativeCredit=new BigDecimal("0");
    					cumulativeDebit=new BigDecimal("0");
    				}
    				Balance newBalance=new Balance();
    				newBalance.setUpdateTime(TimeUtil.getCurrentTime17ByMillis());
    				newBalance.setSubjectId(subId);
    				newBalance.setSubjectName(sub.getComment());
    				newBalance.setSubjectCode(sub.getCode());
    				newBalance.setInitMonCredit(initMonCredit);
    				newBalance.setInitMonDebit(initMonDebit);
    				newBalance.setSettleTime(settleTime);
    				newBalance.setOrgId(companyId);
    				
    				if(sub.getIsLeaf().equals("0")){
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt,subject s where vt.subject_id=s.id and vt.settle_time like '"+settleTime.substring(0,6)+"%' and vt.org_id='"+companyId+"' and s.code like '"+subCode+"%'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    					resultList=generateSummary(companyId, settleTime, oldSettleDate, subId, resultList);
    				}
    				else{
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt where vt.settle_time like '"+settleTime.substring(0,6)+"%'  and vt.org_id='"+companyId+"' and vt.subject_id='"+subId+"'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    				}
    				
    				
    				
    				
    				
    			}
    		}
    		resultList_1 = resultList;
    		result_code1 = resultList;
    		
        }
        
        public List getResult_code1() {
			return result_code1;
        	
        }
    	
    }
    
    class Worker2Thread extends Thread{
    	
    	String companyId;
        String settleTime;
        String oldSettleDate;
        String parentId;
        
    	public Worker2Thread(String companyId,String settleTime,String oldSettleDate,String parentId) {
       	 this.companyId=companyId;
       	 this.settleTime=settleTime;
       	 this.oldSettleDate=oldSettleDate;
       	 this.parentId=parentId;
   	}
    	public void run() {
            // TODO Auto-generated method stub
        	
        	List resultList = new ArrayList(); 
        	Map param=new HashMap();
    		Map balanceParam=new HashMap();
    		List<Balance> balanceList=new ArrayList();
    		param.put("parentId", parentId);
    		param.put("orgId", companyId);
    		param.put("code", "2");
    		List<Subject> subjectList=subjectDao.queryCodeLikeSubjects(param);
    		String subId=null;
    		String subCode=null;
    		Integer accountDirection=1;
    		if(subjectList!=null){
    			for(Subject sub:subjectList){
    				subId=sub.getId();
    				subCode=sub.getCode();
    				accountDirection=sub.getAccountDirection();
    				balanceParam.put("subjectId", subId);
    				balanceParam.put("orgId", companyId);
    				balanceParam.put("settleTime", oldSettleDate);
    				balanceList=balanceDao.getListBalance(balanceParam);
    				Balance oldPo=null;
    				//if this is a new subject, then the old po must be null and we insert into all zero
    				if(balanceList.isEmpty()||balanceList==null||balanceList.get(0)==null){
    					 oldPo=new Balance();
    					 oldPo.setFinalMonCredit(new BigDecimal("0"));
    					 oldPo.setFinalMonDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeCredit(new BigDecimal("0"));
    					 oldPo.setSettleTime(oldSettleDate);
    				}
    				else{
    					 oldPo=balanceList.get(0);					
    				}
    				
    				BigDecimal initMonCredit=oldPo.getFinalMonCredit();
    				BigDecimal initMonDebit=oldPo.getFinalMonDebit();
    				BigDecimal cumulativeCredit=oldPo.getCumulativeCredit();
    				BigDecimal cumulativeDebit=oldPo.getCumulativeDebit();
    				//骞村垵 鏈勾鍙戠敓棰濋噸缃�
    				if(oldPo.getSettleTime().substring(4, 6).equals("12")){
    					cumulativeCredit=new BigDecimal("0");
    					cumulativeDebit=new BigDecimal("0");
    				}
    				Balance newBalance=new Balance();
    				newBalance.setUpdateTime(TimeUtil.getCurrentTime17ByMillis());
    				newBalance.setSubjectId(subId);
    				newBalance.setSubjectName(sub.getComment());
    				newBalance.setSubjectCode(sub.getCode());
    				newBalance.setInitMonCredit(initMonCredit);
    				newBalance.setInitMonDebit(initMonDebit);
    				newBalance.setSettleTime(settleTime);
    				newBalance.setOrgId(companyId);
    				
    				if(sub.getIsLeaf().equals("0")){
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt,subject s where vt.subject_id=s.id and vt.settle_time like '"+settleTime.substring(0,6)+"%' and vt.org_id='"+companyId+"' and s.code like '"+subCode+"%'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    					resultList=generateSummary(companyId, settleTime, oldSettleDate, subId, resultList);
    				}
    				else{
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt where vt.settle_time like '"+settleTime.substring(0,6)+"%'  and vt.org_id='"+companyId+"' and vt.subject_id='"+subId+"'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    				}
    				
    				
    				
    				
    				
    			}
    		}
    		resultList_2 = resultList;
        }
    }
    
    class Worker3Thread extends Thread{
    	
    	String companyId;
        String settleTime;
        String oldSettleDate;
        String parentId;
        
    	public Worker3Thread(String companyId,String settleTime,String oldSettleDate,String parentId) {
       	 this.companyId=companyId;
       	 this.settleTime=settleTime;
       	 this.oldSettleDate=oldSettleDate;
       	 this.parentId=parentId;
   	}
    	public void run() {
            // TODO Auto-generated method stub
        	
        	List resultList = new ArrayList(); 
        	Map param=new HashMap();
    		Map balanceParam=new HashMap();
    		List<Balance> balanceList=new ArrayList();
    		param.put("parentId", parentId);
    		param.put("orgId", companyId);
    		param.put("code", "3");
    		List<Subject> subjectList=subjectDao.queryCodeLikeSubjects(param);
    		String subId=null;
    		String subCode=null;
    		Integer accountDirection=1;
    		if(subjectList!=null){
    			for(Subject sub:subjectList){
    				subId=sub.getId();
    				subCode=sub.getCode();
    				accountDirection=sub.getAccountDirection();
    				balanceParam.put("subjectId", subId);
    				balanceParam.put("orgId", companyId);
    				balanceParam.put("settleTime", oldSettleDate);
    				balanceList=balanceDao.getListBalance(balanceParam);
    				Balance oldPo=null;
    				//if this is a new subject, then the old po must be null and we insert into all zero
    				if(balanceList.isEmpty()||balanceList==null||balanceList.get(0)==null){
    					 oldPo=new Balance();
    					 oldPo.setFinalMonCredit(new BigDecimal("0"));
    					 oldPo.setFinalMonDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeCredit(new BigDecimal("0"));
    					 oldPo.setSettleTime(oldSettleDate);
    				}
    				else{
    					 oldPo=balanceList.get(0);					
    				}
    				
    				BigDecimal initMonCredit=oldPo.getFinalMonCredit();
    				BigDecimal initMonDebit=oldPo.getFinalMonDebit();
    				BigDecimal cumulativeCredit=oldPo.getCumulativeCredit();
    				BigDecimal cumulativeDebit=oldPo.getCumulativeDebit();
    				//骞村垵 鏈勾鍙戠敓棰濋噸缃�
    				if(oldPo.getSettleTime().substring(4, 6).equals("12")){
    					cumulativeCredit=new BigDecimal("0");
    					cumulativeDebit=new BigDecimal("0");
    				}
    				Balance newBalance=new Balance();
    				newBalance.setUpdateTime(TimeUtil.getCurrentTime17ByMillis());
    				newBalance.setSubjectId(subId);
    				newBalance.setSubjectName(sub.getComment());
    				newBalance.setSubjectCode(sub.getCode());
    				newBalance.setInitMonCredit(initMonCredit);
    				newBalance.setInitMonDebit(initMonDebit);
    				newBalance.setSettleTime(settleTime);
    				newBalance.setOrgId(companyId);
    				
    				if(sub.getIsLeaf().equals("0")){
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt,subject s where vt.subject_id=s.id and vt.settle_time like '"+settleTime.substring(0,6)+"%' and vt.org_id='"+companyId+"' and s.code like '"+subCode+"%'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    					resultList=generateSummary(companyId, settleTime, oldSettleDate, subId, resultList);
    				}
    				else{
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt where vt.settle_time like '"+settleTime.substring(0,6)+"%'  and vt.org_id='"+companyId+"' and vt.subject_id='"+subId+"'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    				}
    				
    				
    				
    				
    				
    			}
    		}
    		resultList_3 = resultList;
        }
    }
    
    class Worker4Thread extends Thread{

    	String companyId;
        String settleTime;
        String oldSettleDate;
        String parentId;
        
    	public Worker4Thread(String companyId,String settleTime,String oldSettleDate,String parentId) {
       	 this.companyId=companyId;
       	 this.settleTime=settleTime;
       	 this.oldSettleDate=oldSettleDate;
       	 this.parentId=parentId;
    	}
    	
    	public void run() {
            // TODO Auto-generated method stub
        	
        	List resultList = new ArrayList(); 
        	Map param=new HashMap();
    		Map balanceParam=new HashMap();
    		List<Balance> balanceList=new ArrayList();
    		param.put("parentId", parentId);
    		param.put("orgId", companyId);
    		param.put("code", "4");
    		List<Subject> subjectList=subjectDao.queryCodeLikeSubjects(param);
    		String subId=null;
    		String subCode=null;
    		Integer accountDirection=1;
    		if(subjectList!=null){
    			for(Subject sub:subjectList){
    				subId=sub.getId();
    				subCode=sub.getCode();
    				accountDirection=sub.getAccountDirection();
    				balanceParam.put("subjectId", subId);
    				balanceParam.put("orgId", companyId);
    				balanceParam.put("settleTime", oldSettleDate);
    				balanceList=balanceDao.getListBalance(balanceParam);
    				Balance oldPo=null;
    				//if this is a new subject, then the old po must be null and we insert into all zero
    				if(balanceList.isEmpty()||balanceList==null||balanceList.get(0)==null){
    					 oldPo=new Balance();
    					 oldPo.setFinalMonCredit(new BigDecimal("0"));
    					 oldPo.setFinalMonDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeCredit(new BigDecimal("0"));
    					 oldPo.setSettleTime(oldSettleDate);
    				}
    				else{
    					 oldPo=balanceList.get(0);					
    				}
    				
    				BigDecimal initMonCredit=oldPo.getFinalMonCredit();
    				BigDecimal initMonDebit=oldPo.getFinalMonDebit();
    				BigDecimal cumulativeCredit=oldPo.getCumulativeCredit();
    				BigDecimal cumulativeDebit=oldPo.getCumulativeDebit();
    				//骞村垵 鏈勾鍙戠敓棰濋噸缃�
    				if(oldPo.getSettleTime().substring(4, 6).equals("12")){
    					cumulativeCredit=new BigDecimal("0");
    					cumulativeDebit=new BigDecimal("0");
    				}
    				Balance newBalance=new Balance();
    				newBalance.setUpdateTime(TimeUtil.getCurrentTime17ByMillis());
    				newBalance.setSubjectId(subId);
    				newBalance.setSubjectName(sub.getComment());
    				newBalance.setSubjectCode(sub.getCode());
    				newBalance.setInitMonCredit(initMonCredit);
    				newBalance.setInitMonDebit(initMonDebit);
    				newBalance.setSettleTime(settleTime);
    				newBalance.setOrgId(companyId);
    				
    				if(sub.getIsLeaf().equals("0")){
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt,subject s where vt.subject_id=s.id and vt.settle_time like '"+settleTime.substring(0,6)+"%' and vt.org_id='"+companyId+"' and s.code like '"+subCode+"%'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    					resultList=generateSummary(companyId, settleTime, oldSettleDate, subId, resultList);
    				}
    				else{
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt where vt.settle_time like '"+settleTime.substring(0,6)+"%'  and vt.org_id='"+companyId+"' and vt.subject_id='"+subId+"'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    				}
    				
    				
    				
    				
    				
    			}
    		}
    		resultList_4 = resultList;
        }
    }
	
    class Worker5Thread extends Thread{
    	String companyId;
        String settleTime;
        String oldSettleDate;
        String parentId;
        
    	public Worker5Thread(String companyId,String settleTime,String oldSettleDate,String parentId) {
       	 this.companyId=companyId;
       	 this.settleTime=settleTime;
       	 this.oldSettleDate=oldSettleDate;
       	 this.parentId=parentId;}
    	
    	public void run() {
            // TODO Auto-generated method stub
        	
        	List resultList = new ArrayList(); 
        	Map param=new HashMap();
    		Map balanceParam=new HashMap();
    		List<Balance> balanceList=new ArrayList();
    		param.put("parentId", parentId);
    		param.put("orgId", companyId);
    		param.put("code", "5");
    		List<Subject> subjectList=subjectDao.queryCodeLikeSubjects(param);
    		String subId=null;
    		String subCode=null;
    		Integer accountDirection=1;
    		if(subjectList!=null){
    			for(Subject sub:subjectList){
    				subId=sub.getId();
    				subCode=sub.getCode();
    				accountDirection=sub.getAccountDirection();
    				balanceParam.put("subjectId", subId);
    				balanceParam.put("orgId", companyId);
    				balanceParam.put("settleTime", oldSettleDate);
    				balanceList=balanceDao.getListBalance(balanceParam);
    				Balance oldPo=null;
    				//if this is a new subject, then the old po must be null and we insert into all zero
    				if(balanceList.isEmpty()||balanceList==null||balanceList.get(0)==null){
    					 oldPo=new Balance();
    					 oldPo.setFinalMonCredit(new BigDecimal("0"));
    					 oldPo.setFinalMonDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeDebit(new BigDecimal("0"));
    					 oldPo.setCumulativeCredit(new BigDecimal("0"));
    					 oldPo.setSettleTime(oldSettleDate);
    				}
    				else{
    					 oldPo=balanceList.get(0);					
    				}
    				
    				BigDecimal initMonCredit=oldPo.getFinalMonCredit();
    				BigDecimal initMonDebit=oldPo.getFinalMonDebit();
    				BigDecimal cumulativeCredit=oldPo.getCumulativeCredit();
    				BigDecimal cumulativeDebit=oldPo.getCumulativeDebit();
    				//骞村垵 鏈勾鍙戠敓棰濋噸缃�
    				if(oldPo.getSettleTime().substring(4, 6).equals("12")){
    					cumulativeCredit=new BigDecimal("0");
    					cumulativeDebit=new BigDecimal("0");
    				}
    				Balance newBalance=new Balance();
    				newBalance.setUpdateTime(TimeUtil.getCurrentTime17ByMillis());
    				newBalance.setSubjectId(subId);
    				newBalance.setSubjectName(sub.getComment());
    				newBalance.setSubjectCode(sub.getCode());
    				newBalance.setInitMonCredit(initMonCredit);
    				newBalance.setInitMonDebit(initMonDebit);
    				newBalance.setSettleTime(settleTime);
    				newBalance.setOrgId(companyId);
    				
    				if(sub.getIsLeaf().equals("0")){
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt,subject s where vt.subject_id=s.id and vt.settle_time like '"+settleTime.substring(0,6)+"%' and vt.org_id='"+companyId+"' and s.code like '"+subCode+"%'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    					resultList=generateSummary(companyId, settleTime, oldSettleDate, subId, resultList);
    				}
    				else{
    					String sql="select ifnull(sum(credit),0),ifnull(sum(debit),0) from voucher_detail vt where vt.settle_time like '"+settleTime.substring(0,6)+"%'  and vt.org_id='"+companyId+"' and vt.subject_id='"+subId+"'";
    					List sumResultL=balanceDao.execSql(sql);
    					Object[] sumAarry=(Object[]) sumResultL.get(0);
    					BigDecimal sumCredit=(BigDecimal) sumAarry[0];
    					BigDecimal sumDebit=(BigDecimal) sumAarry[1];
    					BigDecimal newCumulativeCredit=cumulativeCredit.add(sumCredit);
    					BigDecimal newCumulativeDebit=cumulativeDebit.add(sumDebit);
    					BigDecimal finalMonCredit=initMonCredit.add(sumCredit);
    					BigDecimal finalMonDebit=initMonDebit.add(sumDebit);
    					
    					newBalance.setCumulativeCredit(newCumulativeCredit);
    					newBalance.setCumulativeDebit(newCumulativeDebit);
    					newBalance.setCurrentMonCredit(sumCredit);
    					newBalance.setCurrentMonDebit(sumDebit);
    					newBalance.setFinalMonCredit(finalMonCredit);
    					newBalance.setFinalMonDebit(finalMonDebit);
    					newBalance.setInitBalance(initMonDebit.subtract(initMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setEndBalance(finalMonDebit.subtract(finalMonCredit).multiply(new BigDecimal(accountDirection)));
    					newBalance.setAccountDirection(sub.getAccountDirection());
    					//鏌ュ嚭骞村垵浣欓
    					if(!settleTime.substring(4, 6).equals("01")){
    						String queryInitYearSql="select ifnull(init_balance,0) from balance where org_id='"+companyId+"' and settle_time='"+settleTime.substring(0,4)+""+"0131' and subject_id='"+sub.getId()+"'";
    						List initBalanceResultL=balanceDao.execSql(queryInitYearSql);
    						if(initBalanceResultL.isEmpty()){
    							newBalance.setInitYearBalance(new BigDecimal("0"));
    						}
    						else{
//    							Object[] initAarry=(Object[]) initBalanceResultL.get(0);
//    							BigDecimal initValue=(BigDecimal) initAarry[0];
    							BigDecimal initValue=(BigDecimal) initBalanceResultL.get(0);
    							newBalance.setInitYearBalance(initValue);
    						}
    					}
    					else{
    						newBalance.setInitYearBalance(newBalance.getInitBalance());
    					}
    					
    					resultList.add(newBalance);
    				}
    				
    				
    				
    				
    				
    			}
    		}
    		resultList_5 = resultList;
        }
    }
	
}
