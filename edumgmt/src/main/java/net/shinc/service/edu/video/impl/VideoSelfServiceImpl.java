package net.shinc.service.edu.video.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.shinc.orm.mybatis.bean.common.AdminUser;
import net.shinc.orm.mybatis.bean.common.QueryBean;
import net.shinc.orm.mybatis.bean.edu.VideoBase;
import net.shinc.orm.mybatis.bean.edu.VideoBaseKeywordKey;
import net.shinc.orm.mybatis.bean.edu.VideoBaseKnowledgePointKey;
import net.shinc.orm.mybatis.bean.edu.VideoDetail;
import net.shinc.orm.mybatis.bean.edu.VideoSelf;
import net.shinc.orm.mybatis.mappers.edu.VideoBaseKeywordMapper;
import net.shinc.orm.mybatis.mappers.edu.VideoBaseKnowledgePointMapper;
import net.shinc.orm.mybatis.mappers.edu.VideoBaseMapper;
import net.shinc.orm.mybatis.mappers.edu.VideoDetailMapper;
import net.shinc.orm.mybatis.mappers.edu.VideoSelfMapper;
import net.shinc.service.edu.video.VideoBaseService;
import net.shinc.service.edu.video.VideoSelfService;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: VideoSelfServiceImpl
 * @Description: 自编题服务接口实现
 * @author hushichong
 * @date 2015年7月31日 下午5:48:34
 */
@Service
public class VideoSelfServiceImpl implements VideoSelfService {

	@Autowired
	private VideoSelfMapper videoSelfMapper;
	@Autowired
	private VideoBaseMapper videoBaseMapper;
	@Autowired
	private VideoDetailMapper videoDetailMapper;
	@Autowired
	private VideoBaseKnowledgePointMapper videoBaseKnowledgePointMapper;
	@Autowired
	private VideoBaseKeywordMapper videoBaseKeywordMapper;
	@Autowired
	private VideoBaseService videoBaseService;

	@Override
	public Map insertVideoSelf(VideoSelf videoSelf) {
		Map map = new HashMap();
		VideoBase videoBase = videoSelf.getVideoBase();
		videoBase.setAdminUserId(AdminUser.getCurrentUser().getId());
		videoBase.setUpdatetime(new Date());
		videoBase.setProfile(StringUtils.trim(videoBase.getProfile()));
		videoBase.setQuestionId(String.valueOf(System.currentTimeMillis()));
		videoBaseMapper.insertVideoBase(videoBase);
		map.put("videoBaseId", videoBase.getId());
		videoSelf.setVideoBaseId(videoBase.getId());
		// 插入视频详情
		if (videoSelf.getVideoBase() != null && videoSelf.getVideoBase().getVideoDetailList() != null && videoSelf.getVideoBase().getVideoDetailList().size() > 0) {
			for (VideoDetail vd : (List<VideoDetail>) videoSelf.getVideoBase().getVideoDetailList()) {
				vd.setVideoBaseId(videoBase.getId());
				vd.setUpdatetime(new Date());
				videoDetailMapper.insertVideoDetail(vd);
			}
		}

		// 插入知识点关系
		if (StringUtils.isNotEmpty(videoSelf.getKnowledgePointIds())) {
			for (String id : StringUtils.split(videoSelf.getKnowledgePointIds(), ",")) {
				VideoBaseKnowledgePointKey videoBaseKnowledgePointKey = new VideoBaseKnowledgePointKey();
				videoBaseKnowledgePointKey.setVideoBaseId(videoBase.getId());
				videoBaseKnowledgePointKey.setKnowledgePointId(Integer.valueOf(id));
				videoBaseKnowledgePointMapper.insert(videoBaseKnowledgePointKey);
			}
		}

		// 插入关键字关系
		if (StringUtils.isNotEmpty(videoSelf.getKewordIds())) {
			for (String keywordId : StringUtils.split(videoSelf.getKewordIds(), ",")) {
				VideoBaseKeywordKey videoBaseKeywordKey = new VideoBaseKeywordKey();
				videoBaseKeywordKey.setVideoBaseId(videoBase.getId());
				videoBaseKeywordKey.setKeywordId(Integer.valueOf(keywordId));
				videoBaseKeywordMapper.insertVideoKeyword(videoBaseKeywordKey);
			}
		}
		videoSelfMapper.insertVideoSelf(videoSelf);
		return map;
	}

	@Override
	public Map updateVideoSelf(VideoSelf videoSelf) {
		Map map = new HashMap();
		VideoBase videoBase = videoSelf.getVideoBase();
		videoBase.setUpdatetime(new Date());
		videoBase.setProfile(StringUtils.trim(videoBase.getProfile()));
		videoSelfMapper.updateVideoSelf(videoSelf);
		videoBaseMapper.updateVideoBase(videoBase);
		map.put("videoBaseId", videoBase.getId());
		// 更新视频详情
		if (videoSelf.getVideoBase() != null && videoSelf.getVideoBase().getVideoDetailList() != null && videoSelf.getVideoBase().getVideoDetailList().size() > 0) {
			for (VideoDetail vd : (List<VideoDetail>) videoSelf.getVideoBase().getVideoDetailList()) {
				vd.setVideoBaseId(videoBase.getId());
				vd.setUpdatetime(new Date());
				videoDetailMapper.updateVideoDetail(vd);
			}
		}

		// 更新知识点关系
		if (StringUtils.isNotEmpty(videoSelf.getKnowledgePointIds())) {
			
			VideoBaseKnowledgePointKey videoBaseKnowledgePointKey = new VideoBaseKnowledgePointKey();
			videoBaseKnowledgePointKey.setVideoBaseId(videoBase.getId());	
			videoBaseKnowledgePointMapper.deleteVideoBaseKnowledgePoint(videoBaseKnowledgePointKey);
			
			for (String id : StringUtils.split(videoSelf.getKnowledgePointIds(), ",")) {
				videoBaseKnowledgePointKey = new VideoBaseKnowledgePointKey();
				videoBaseKnowledgePointKey.setVideoBaseId(videoBase.getId());
				videoBaseKnowledgePointKey.setKnowledgePointId(Integer.valueOf(id));
				
				videoBaseKnowledgePointMapper.insert(videoBaseKnowledgePointKey);
			}
		}

		// 更新关键字关系
		if(StringUtils.isNotEmpty(videoSelf.getKewordIds())){
			VideoBaseKeywordKey videoBaseKeywordKey = new VideoBaseKeywordKey();
			videoBaseKeywordKey.setVideoBaseId(videoBase.getId());	
			videoBaseKeywordMapper.deleteVideoKeywordById(videoBaseKeywordKey);
			
			for (String keywordId : StringUtils.split(videoSelf.getKewordIds(), ",")) {
				videoBaseKeywordKey = new VideoBaseKeywordKey();
				videoBaseKeywordKey.setVideoBaseId(videoBase.getId());	
				videoBaseKeywordKey.setKeywordId(Integer.valueOf(keywordId));
				videoBaseKeywordMapper.insertVideoKeyword(videoBaseKeywordKey);
			}
		}	
		return map;
	}

	@Override
	public VideoSelf getByVideoSelfById(Integer id) {
		return videoSelfMapper.getByVideoSelfById(id);
	}

	@Override
	public List<VideoSelf> getVideoSelfList(VideoSelf videoSelf) {
		return videoSelfMapper.getVideoSelfList(videoSelf);
	}

	@Override
	public VideoSelf getVideoSelf(VideoSelf videoSelf) {
		List list = getVideoSelfList(videoSelf);
		if (list != null && list.size() == 1) {
			return (VideoSelf) list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void deleteVideoSelfById(Integer id) {
		videoSelfMapper.deleteVideoSelfById(id);

	}

	@Override
	public List<Map> getVideoSelfAndRelevantInfoList(QueryBean queryBean,RowBounds rowBounds) {
		List<Map> list = videoSelfMapper.getVideoSelfAndRelevantInfoList(queryBean,rowBounds);
		List<Map> list2 = videoBaseService.appendQrUrl(list);
		return list2;
	}

}