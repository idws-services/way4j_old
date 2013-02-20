package way4j.tools.listeners;

import org.hibernate.SessionFactory;

import way4j.tools.utils.GenericUtils;

public class OpenSessionInViewFilter extends org.springframework.orm.hibernate3.support.OpenSessionInViewFilter{
	@Override
	protected SessionFactory lookupSessionFactory() {
		return GenericUtils.springContext.getBean(SessionFactory.class);
	}
}
