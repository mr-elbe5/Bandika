package de.bandika.http.taglib;

import de.bandika.http.*;
import de.bandika.base.Controller;
import de.bandika.base.RightException;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class SetMasterTag extends BaseTag {

	String master=null;

	public void setMaster(String master) {
		this.master = master;
	}

	public int doStartTag() throws JspException {
		RequestData rdata=HttpHelper.getRequestData((HttpServletRequest)context.getRequest());
		if (rdata.getProcessingState()==RequestData.PROCESSED_MASTER){
			return EVAL_BODY_INCLUDE;
		}
		try{
			if (rdata.getProcessingState()<RequestData.PROCESSED_CONTROLLER){
				try{
					HttpServletRequest request=(HttpServletRequest)context.getRequest();
					rdata.setCurrentJspFromRequest(request);
					SessionData sdata=HttpHelper.getSessionData(request);
					Response rsp = Controller.getResponse(rdata,sdata);
          rdata.setProcessingState(RequestData.PROCESSED_CONTROLLER);
					if (rsp!=null && rsp.getType()==Response.TYPE_JSP){
						JspResponse prsp=(JspResponse)rsp;
						if (prsp.getJsp()!=null && !prsp.getJsp().equals(rdata.getCurrentJsp())){
							rdata.setCurrentJsp(prsp.getJsp());
							context.forward(prsp.getJsp());
              return SKIP_BODY;
						}
          }
				}
				catch (RightException re){
					rdata.setCurrentJsp("/_jsp/noaccess.jsp");
				}
				catch (Exception e){
					rdata.setException(e);
					rdata.setCurrentJsp("/_jsp/error.jsp");
				}
			}
			rdata.setProcessingState(RequestData.PROCESSED_MASTER);
			context.include(master);
			return SKIP_BODY;
		}
		catch (Exception e){
			throw new JspException(e);
		}
	}

	public int doEndTag() throws JspException {
		RequestData rdata=HttpHelper.getRequestData((HttpServletRequest)context.getRequest());
		rdata.resetProcessingState();
		return 0;
	}

}
