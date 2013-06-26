package eu.dime.ps.datamining;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dlpo.ImagePost;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.dlpo.VideoPost;
import eu.dime.ps.semantic.util.DateUtils;

public class LivePostDecomposerTest extends Assert {

	LivePostDecomposer decomposer;
	
	@Before
	public void setUp() throws Exception {
		decomposer = new LivePostDecomposer();
	}
	
	@Test
	public void testDecomposeVideoAndImage() throws Exception {
		LivePost livepost = buildLivePost("Love this song!! http://www.youtube.com/watch?v=1qarJZtXyE8&feature=g-all, disc cover: http://violadoresdelverso.atspace.com/violadores-del-verso.jpg #violadores #verso");
		
		List<LivePost> liveposts = decomposer.decompose(livepost);
		assertEquals(4, liveposts.size()); // LivePost, Status, ImagePost, VideoPost
		assertTrue(liveposts.contains(livepost));
		
		LivePost parent = null;
		ImagePost imagePost = null;
		VideoPost videoPost = null;
		for (LivePost lv : liveposts) {
			if (lv instanceof VideoPost) {
				videoPost = (VideoPost) lv;
			} else if (lv instanceof ImagePost) {
				imagePost = (ImagePost) lv;
			} else {
				parent = lv;
			}
		}
		assertEquals(2, parent.getAllRelatedResource_as().count());
		assertEquals("http://violadoresdelverso.atspace.com/violadores-del-verso.jpg", imagePost.getAllDefiningResource().next().toString());
		assertEquals("http://www.youtube.com/watch?v=1qarJZtXyE8&feature=g-all", videoPost.getAllDefiningResource().next().toString());
	}
	
	@Test
	public void testDecomposeCheckin() throws Exception {
		LivePost livepost = buildLivePost("Just arrived in Stuttgart. Intense days of integration and testing ahead!");
		// TODO
	}
	
	private LivePost buildLivePost(String textualContent) {
		LivePost livepost = (new ModelFactory()).getDLPOFactory().createLivePost();
		livepost.setTextualContent(textualContent);
		livepost.setCreated(DateUtils.now());
		return livepost;
	}

}
