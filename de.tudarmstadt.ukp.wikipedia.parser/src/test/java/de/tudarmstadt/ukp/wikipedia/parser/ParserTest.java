package de.tudarmstadt.ukp.wikipedia.parser;

import de.tudarmstadt.ukp.wikipedia.api.WikiConstants;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dav009 on 23/06/2015.
 */
public class ParserTest {

    @Test
    public void testGalleryLinks(){
        String title = "Wikipedia API";

        String LF = "\n";
        String text = "India collided with Asia {{Ma|55|45}} creating the Himalayas; Arabia collided with Eurasia, " +
                "closing the [[Tethys ocean]] and creating the Zagros Mountains, " +
                "around {{Ma|35}}.<ref name=Allen2008>{{cite doi|10.1016/j.palaeo.2008.04.021 }}</ref>\n" +
                "<gallery>\n" +
                "|35  million years ago\n" +
                "|20  million years ago\n" +
                "</gallery>";

        MediaWikiParserFactory pf = new MediaWikiParserFactory(WikiConstants.Language.english);
        MediaWikiParser parser = pf.createParser();

        ParsedPage pp = parser.parse(text);

        for (Section s : pp.getSections()){
            for (Link link : s.getLinks()) {
                assert(!link.getTarget().equals(""));
            }
        }


    }

    public boolean findLinkInList(Link l, Collection<Link> list){
         for(Link currentLink:list){
             if (l.equals(currentLink)) return true;
         }
        return false;
    }


    @Test
    public void testExtractingLinksFromGallery(){

        String text = "==History==\n" +
                "<gallery>\n" +
                "Image:Buckingham Branch Railroad GP16 rebuild.JPG|BB 2, a [[GP16]], getting a new coat of paint at [[Dillwyn, Virginia]].\n" +
                "Image:BBRR4 Dillwyn WJGrimes.JPG|BBRR 4, an [[RS-4-TC]], with a new coat of paint at [[Dillwyn, Virginia]].\n" +
                "Image:BB7 Louisa WJGrimes.JPG|BB 7, a [[GP40]], heading east at [[Louisa, Virginia]].\n" +
                "Image:BBRR8 Doswell WJGrimes.JPG|BB 8, a GP16 in GRIV paint at [[Doswell, Virginia]].\n" +
                "Image:Bb 13 augusta co-op.jpg|BB 13 switches Augusta CO-OP in [[Staunton, Virginia]].\n" +
                "Image:Bb_7_svrr_staunton,_va_08292010.jpg|BB 7 with sisters, south on the [[Shenandoah Valley Railroad (short-line)|Shenandoah Valley Railroad]] in [[Staunton, Virginia]]. Having just made a pick up of Empty Cars to take West.\n" +
                "Image:image_with_no_par.jpg\n" +
                "Image:image_with_par.jpg|Something [[Spiderman]]\n" +
                "</gallery>";

        MediaWikiParserFactory pf = new MediaWikiParserFactory(WikiConstants.Language.english);
        MediaWikiParser parser = pf.createParser();
        ParsedPage pp = parser.parse(text);
        Paragraph testPar;
        testPar = pp.getSections().get(0).getParagraphs().get(0);

        assert(this.findLinkInList(new Link(testPar, new Span(8, 12), "GP16",  Link.type.INTERNAL, new ArrayList<String>()), testPar.getLinks()));
        assert(this.findLinkInList(new Link(testPar, new Span(45, 62), "Dillwyn,_Virginia",  Link.type.INTERNAL, new ArrayList<String>()), testPar.getLinks()));
        assert(this.findLinkInList(new Link(testPar, new Span(312, 338), "Shenandoah_Valley_Railroad_(short-line)",  Link.type.INTERNAL, new ArrayList<String>()), testPar.getLinks()));
        assert(this.findLinkInList(new Link(testPar, new Span(428, 437), "Spiderman",  Link.type.INTERNAL, new ArrayList<String>()), testPar.getLinks()));

        for (Link l: testPar.getLinks()){
            int start = l.getPos().getStart();
            int end = l.getPos().getEnd();
            if(start!=end){
                assert(testPar.getText().substring(start, end).equals(l.getText()));
            }
        }
        assert(testPar.getText().contains("a GP16, getting a new coat of paint at "));

    }
}
