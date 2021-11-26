package com.tanhua.dubbo;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongos.*;
import jdk.nashorn.internal.runtime.regexp.joni.constants.internal.OPCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void test() {

        Soul soul = new Soul();
        soul.setId("1");
        soul.setName("初级灵魂题");
        soul.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_01.png");
        soul.setLevel("初级");
        soul.setIsLock(0);
        soul.setStar(2);
        mongoTemplate.save(soul);

    }

    @Test
    public void test12() {

        Soul soul = new Soul();
        soul.setId("2");
        soul.setName("中级灵魂题");
        soul.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_02.png");
        soul.setLevel("中级");
        soul.setIsLock(0);
        soul.setStar(3);
        mongoTemplate.save(soul);

    }

    @Test
    public void test122() {

        Soul soul = new Soul();
        soul.setId("3");
        soul.setName("高级灵魂题");
        soul.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_03.png");
        soul.setLevel("中级");
        soul.setIsLock(0);
        soul.setStar(5);
        mongoTemplate.save(soul);

    }



    @Test
    public void test1() {

        Questions questions = new Questions();
        questions.setId("3");
        questions.setQuestion("假如你在你房间墙壁上发现一个了小孔，你希望从这个小孔中看如下到什么样的场景？");


        List<Options> list=new ArrayList<>();
        Options options = new Options();
        options.setId("1");
        options.setOption("正在拥抱的一对恋人,一家人正其乐融融地吃晚餐");
        list.add(options);



        Options options1 = new Options();
        options1.setId("2");
        options1.setOption("拿起你的大飞机 冲冲冲");
        list.add(options1);


        Options options2 = new Options();
        options2.setId("3");
        options2.setOption("拿起你的大飞机 干干干");
        list.add(options2);


        Options options3 = new Options();
        options3.setId("4");
        options3.setOption("拿起你的大飞机 射火箭");
        list.add(options3);



        questions.setOptions(list);

        mongoTemplate.save(questions);
    }

    @Test
    public void test213() {



//        List<Options> list=new ArrayList<>();
//        Options options = new Options();
//        options.setId("1");
//        options.setOption("正在拥抱的一对恋人,一家人正其乐融融地吃晚餐");
//        list.add(options);
//
//
////        questions.setOptions(list);
//
//        mongoTemplate.save(questions);
    }

    @Test
    public void ass(){
//        Query id = Query.query(Criteria.where("id").is("1"));
//
//        Soul one = mongoTemplate.findOne(id, Soul.class);
//        System.out.println(one);

//        System.out.println(mongoTemplate.findAll(Soul.class));


        Query id = Query.query(Criteria.where("id").is("1"));
        Soul one = mongoTemplate.findOne(id, Soul.class);
        List<Questions> questions = mongoTemplate.findAll(Questions.class);
        one.setQuestions(questions);
        System.out.println(one);
    }

    @Test
    public void submit(){

        Report report = new Report();
        report.setId("1");
        report.setConclusion("狮子型：性格为充满自信、竞争心强、主动且企图心强烈，是个有决断力的领导者。一般而言，狮子型的人胸怀大志，勇于冒险，看问题能够直指核心，并对目标全力以赴。他们在领导风格及决策上，强调权威与果断，擅长危机处理，此种性格最适合开创性与改革性的工作。");
        report.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/rabbit.png");

        List<Dimensions> list=new ArrayList<>();
        Dimensions dimensions = new Dimensions();
        dimensions.setKey("判断");
        dimensions.setValue("60%");

        Dimensions dimensions1 = new Dimensions();
        dimensions1.setKey("判断");
        dimensions1.setValue("60%");

        Dimensions dimensions2 = new Dimensions();
        dimensions2.setKey("判断");
        dimensions2.setValue("60%");

        Dimensions dimensions3 = new Dimensions();
        dimensions3.setKey("判断");
        dimensions3.setValue("60%");

        list.add(dimensions);
        list.add(dimensions1);
        list.add(dimensions2);
        list.add(dimensions3);
        report.setDimensions(list);

        mongoTemplate.save(report);

        Report report1 = new Report();
        report1.setId("2");
        report1.setConclusion("狐狸型 ：人际关系能力极强，擅长以口语表达感受而引起共鸣，很会激励并带动气氛。他们喜欢跟别人互动，重视群体的归属感，基本上是比较「人际导向」。由于他们富同理心并乐于分享，具有很好的亲和力，在服务业、销售业、传播业及公共关系等领域中，狐狸型的领导者都有很杰出的表现。");
        report1.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/owl.png");
        mongoTemplate.save(report1);

        Report report2 = new Report();
        report2.setId("3");
        report2.setConclusion("白兔型：平易近人、敦厚可靠、避免冲突与不具批判性。在行为上，表现出不慌不忙、冷静自持的态度。他们注重稳定与中长程规划，现实生活中，常会反思自省并以和谐为中心，即使面对困境，亦能泰然自若，从容应付。");
        report2.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/fox.png");
        mongoTemplate.save(report2);

    }
}
