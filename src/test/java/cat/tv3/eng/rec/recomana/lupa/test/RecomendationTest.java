/**
Copyright 2014 Jaume Jané 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package cat.tv3.eng.rec.recomana.lupa.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cat.tv3.eng.rec.recomana.lupa.engine.KLDdistance;
import cat.tv3.eng.rec.recomana.lupa.engine.LupaItem;

public class RecomendationTest {
	
	private final static String text_1 = "Content test 1 example";
	private final static String text_2 = "Here content test 2 example";	
	
	@Test
	public void testRecomendation() throws InterruptedException {
		
		List<String> words_test_1 = new ArrayList<String>();		
		words_test_1 = Arrays.asList(text_1.split(" "));		
		LupaItem vocabulary_test_1 = new LupaItem(words_test_1);
		
		List<String> words_test_2 = new ArrayList<String>();		
		words_test_2 = Arrays.asList(text_2.split(" "));
		LupaItem vocabulary_test_2 = new LupaItem(words_test_2);
				
		KLDdistance classificador = new KLDdistance(vocabulary_test_1,vocabulary_test_2);
		System.out.println(classificador.distance());
		test(2.243,classificador.distance());		
		
	}

	protected static void test(Double expected, double distance) {			
		assertEquals(expected, distance, 0.01);		
	}
}
