package org.example.weather;

import java.lang.reflect.Method;

/**
 * Wrapper for {@link SpringMain} which maps the JSR-330 annotations to Spring's annotations.
 * <br>
 * If you are using Spring v3 or above this will not be required because as of v3 the annotations
 * are inter-changeable. This demo is using v2 since v3 isn't out yet, so we're using some hacks
 * to pretend we are on v3 and have that interoperability support.
 * <br>
 * In actuality we are rewriting some bytecode to use the correct annotation names while sticking
 * with Spring v2.
 * <br>
 * Anyways the real demo code is in {@link SpringMain}
 */
public class Main {
	public static void main(String[] args) {
		SpringHacking.InjectToAutowiredMappingClassLoader loader =
				new SpringHacking.InjectToAutowiredMappingClassLoader();
		try {
			Class<?> mainClass = loader.findClass(SpringMain.class.getName());
			Method main = mainClass.getDeclaredMethod("main", String[].class);
			Object[] invokeArgs = {args};
			main.invoke(null, invokeArgs);
		} catch (ReflectiveOperationException ex) {
			ex.printStackTrace();
		}
	}

}
