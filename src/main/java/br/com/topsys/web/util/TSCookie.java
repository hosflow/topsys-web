package br.com.topsys.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class TSCookie {

	public static Cookie getCookie(HttpServletRequest httpServletRequest, String nome) {

		Cookie[] cookies = httpServletRequest.getCookies();

		Cookie donaBenta = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(nome)) {
					donaBenta = cookie;
					break;
				}

			}
		}

		return donaBenta;

	}

	public static void addCookie(HttpServletResponse httpServletResponse, String nome, String valor, Integer duracao) {

		Cookie donaBenta = new Cookie(nome, valor);

		donaBenta.setMaxAge(duracao);

		httpServletResponse.addCookie(donaBenta);

	}

	public static void removeCookie(HttpServletResponse httpServletResponse, String nome) {
		addCookie(httpServletResponse, nome, "", 0);
	}
}
