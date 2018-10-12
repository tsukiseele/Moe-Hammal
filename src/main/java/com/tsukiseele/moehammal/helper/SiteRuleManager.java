package com.tsukiseele.moehammal.helper;

import com.tsukiseele.moecrawler.bean.Site;
import com.tsukiseele.moecrawler.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tsukiseele.moehammal.app.Config.PATH_SOURCE_RULES;

public class SiteRuleManager {
	private static final SiteRuleManager MANAGER = new SiteRuleManager();

	private List<Site> sites = new ArrayList<>();

	private SiteRuleManager() {
		loadRule();
	}

	public void loadRule() {
		sites.clear();
		List<File> rules = FileUtil.scanDirectory(PATH_SOURCE_RULES, ".json");
		if (rules != null)
			for (File rule : rules) {
				try {
					Site site = Site.fromJSON(FileUtil.readText(rule.getAbsolutePath(), "UTF-8"));
					site.setPath(rule.getAbsolutePath());
					sites.add(site);
				} catch (Exception e) {
					System.out.println("规则加载失败：" + rule.getName() + "\n" + e.toString());
				}
			}
	}

	public static SiteRuleManager instance() {
		return MANAGER;
	}

	public List<Site> getSites() {
		return sites;
	}
}
