package br.com.topsys.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class TSCookie {

	public static Cookie getCookie(HttpServletRequest httpServletRequest, String nome) {

		Cookie cookies[] = httpServletRequest.getCookies();

		Cookie donaBenta = null;

		if (cookies != null) {

			for (int x = 0; x < cookies.length; x++) {

				if (cookies[x].getName().equals(nome)) {

					donaBenta = cookies[x];

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

}
