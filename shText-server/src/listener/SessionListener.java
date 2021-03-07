package listener;


import buffer.ChapterBuffer;
import buffer.UserBuffer;

import javax.servlet.http.*;

public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session=se.getSession();
        UserBuffer.sessionMap.put(session.getId(),session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session=se.getSession();
        UserBuffer.sessionMap.remove(session.getId());
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        if(se.getName().equals(UserBuffer.U_SESSION_NAME))
        {
            System.out.println("attribute added");
            UserBuffer.addUserSession(se);
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        if(se.getName().equals(UserBuffer.U_SESSION_NAME))
        {
            System.out.println("attribute removed");
        }
    }


}
