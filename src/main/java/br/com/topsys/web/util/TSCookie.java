package br.com.topsys.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public final class TSCookie {
	
	
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	
	public TSCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;
	}
	
	public Cookie getCookie(String nome) {

		Cookie[] cookies = this.httpServletRequest.getCookies();

		Cookie cookie = null;

		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(nome)) {
					cookie = c;
					break;
				}

			}
		}

		return cookie;

	}
	
	public String getValue(String nome) {
        Cookie cookie = this.getCookie(nome);
		return cookie != null ? cookie.getValue() : null;

	}

	public void add(String nome, String valor, Integer duracao) {

		Cookie cookie = new Cookie(nome, valor);

		cookie.setMaxAge(duracao);
		
		cookie.setHttpOnly(true);

		this.httpServletResponse.addCookie(cookie);

	}

	public void remove(String nome) {
		this.add(nome, "", 0);
	}
}
