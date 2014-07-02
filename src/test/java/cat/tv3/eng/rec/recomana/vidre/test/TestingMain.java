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

package cat.tv3.eng.rec.recomana.vidre.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cat.tv3.eng.rec.recomana.vidre.engine.KLDdistance;
import cat.tv3.eng.rec.recomana.vidre.engine.VidreItem;

public class TestingMain {
	private final static String politica_1 = "La direcció del PSOE es planteja convocar una consulta perquè els seus militants escullin el pròxim secretari general del partit uns dies abans del congrés extraordinari de juliol. L'executiva del partit estudia aquesta possibilitat si així ho demana la majoria del partit, segons asseguren fonts socialistes. Aquesta fórmula comportaria que els militants escollissin amb el seu vot directe el substitut de Rubalcaba. El congrés, posteriorment, el ratificaria i aprovaria la nova executiva. D'aquesta manera, es convocaria primer un Comitè Federal per modificar els estatuts del PSOE per donar legalitat a aquesta consulta. Alguns diputats van engegar dimarts aquesta petició, que satisfaria almenys dos dels qui aspiren a les primàries, els diputats Eduardo Madina i Pedro Sánchez. Madina va dir que veia amb bons ulls el que alguns han anomenat congrés a la gallega, ja que els socialistes gallecs van ser els primers que van assajar amb èxit aquesta fórmula l'estiu passat. Aquesta fórmula és el màxim exemple que els militants protagonitzen el canvi, va destacar Sánchez als passadissos del Congrés.";
	private final static String politica_2 = " La direcció del PSOE es planteja convocar una consulta perquè L'exconseller de la Generalitat Valenciana Rafael Blasco ha estat condemnat a 8 anys de presó i a 20 d'inhabilitació pel cas Cooperació per malversació de fons públics, prevaricació i falsedat. Blasco havia estat jutjat pel Tribunal Superior de Justícia del País Valencià en relació amb un cas de possible desviació de fons públics de la Generalitat concedits l'any 2008, que havien d'anar destinats a cooperació i desenvolupament a Nicaragua." ;
	private final static String oci_cultura = "Des d'aquest dimarts Barcelona acull la subhasta d'art i antiguitats més gran que s'ha fet mai a Catalunya i també en el conjunt de l'estat espanyol. La sala de subhastes Balclis posa a la venda 2.614 lots, entre els quals destaquen la primera pintura cubista que es va exposar a Espanya el 1921, de Francisco Camps Ribera. Una de les joies més destacades és el fons de l'antiquari barceloní Las Meninas, que ha tancat recentment després de ser durant dècades un dels locals més importants del sud d'Europa. La subhasta ha despertat un gran interès internacional, segons ha declarat Enric Carrancó, responsable del departament de pintura de Balclis, que ha dit que s'havien rebut centenars de licitacions prèvies des de diferents països com Austràlia, la Xina, Rússia, Itàlia, França, els Estats Units i Alemanya, tant de comerciants com de particulars. L'antiquari Las Meninas va arribar a ser un dels màxims importadors d'antiguitats. Es va especialitzar en obres procedents d'Anglaterra, França, Bèlgica, Itàlia o Amèrica del Sud, i va diversificar l'oferta dels antiquaris dels anys seixanta a Espanya, que estaven centrats en peces locals. Aquest antiquari va adquirir a París el taller de Pere Pruna i va culminar el 1972 les importacions dels murals que Josep Maria Sert havia pintat per a l'hotel Waldorf Astoria de Nova York. Altres peces importants de la subhasta són un quadre d'Antoni Garcia Lamolla datat el 1935, un pintor que penja al Reina Sofia i el MNAC de Barcelona, de l'òrbita surrealista més pròxima a Dalí, i un Tàpies de la seva millor època. També destaca una col·lecció vintage de 122 records relacionats amb els Beatles, figuretes, entrades i cotxets, amb un preu de sortida de 40.000 euros. Però un dels lots que més han cridat l'atenció és una col·lecció de Montevideo de 23 models d'anatomia humana i patològica en cera i estuc policromat, del primer terç del segle XX (1940-1950), en el qual destaquen quatre figures realistes de cera que recreen diferents formes d'un part. La subhasta s'acabarà demà dijous.";
	private final static String economia_1 = "El Banc d'Espanya preveu que la recuperació de l'economia es continuarà prolongant. Al seu últim butlletí, l'organisme que dirigeix Luis María Linde destaca que aquesta millora té diferents intensitats en funció de l'indicador analitzat. Per exemple, les exportacions i la matriculació de cotxes continuen creixent a bon ritme, però el consum l'ha moderat. El Banc d'Espanya també subratlla l'increment del nombre d'aturats de llarga durada. Ara n'hi ha més de 3.600.000, després que l'últim any la xifra ha augmentat un 3,5%. L'organisme recomana seguir lluitant contra l'elevat endeutament exterior i aplicar mesures perquè l'economia continuï guanyant competitivitat.";
	
	public static void main(String[] args) {
		
		List<String> words_politica_1 = new ArrayList<String>();		
		words_politica_1 = Arrays.asList(politica_1.split(" "));		
		VidreItem vocabulary_politica_1 = new VidreItem(words_politica_1);
		
		List<String> words_politica_2 = new ArrayList<String>();		
		words_politica_2 = Arrays.asList(politica_2.split(" "));
		VidreItem vocabulary_politica_2 = new VidreItem(words_politica_2);
				
		KLDdistance classificador = new KLDdistance(vocabulary_politica_1,vocabulary_politica_2);		
		System.out.println("Distance politica 1<->2 -> " + classificador.distance());
		
		List<String> words_oci_cultura = new ArrayList<String>();		
		words_oci_cultura = Arrays.asList(oci_cultura.split(" "));		
		VidreItem vocabulary_oci_cultura = new VidreItem(words_oci_cultura);
		
		List<String> words_economia_1 = new ArrayList<String>();		
		words_economia_1 = Arrays.asList(economia_1.split(" "));
		VidreItem vocabulary_economia_1 = new VidreItem(words_economia_1);
				
		classificador = new KLDdistance(vocabulary_politica_1,vocabulary_oci_cultura);			
		System.out.println("Distance politica 1 -> ocicultura " + classificador.distance());
		
		classificador = new KLDdistance(vocabulary_politica_2,vocabulary_oci_cultura);			
		System.out.println("Distance politica 2 -> ocicultura " + classificador.distance());
		
		classificador = new KLDdistance(vocabulary_politica_1,vocabulary_economia_1);			
		System.out.println("Distance politica 1 -> economia " + classificador.distance());
		
		classificador = new KLDdistance(vocabulary_politica_2,vocabulary_economia_1);			
		System.out.println("Distance politica 2 -> economia " + classificador.distance());
		
		classificador = new KLDdistance(vocabulary_oci_cultura,vocabulary_economia_1);			
		System.out.println("Distance oci_cultura -> economia " + classificador.distance());	
		
	}
	
}
