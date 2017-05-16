/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.util;

import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/25/2017.
 */
public class TestUtils extends TestCase {
    public static class TestInputStream extends InputStream {
        public int read () {
            throw new IllegalStateException("Not implemented");
        }

        public void close () throws IOException {
            throw new IOException("test");
        }
    }

    public static class TestOutputStream extends OutputStream {
        public void write (int value) {
            throw new IllegalStateException("not implemented");
        }

        public void close () throws IOException {
            throw new IOException("test");
        }
    }

    private Utils utils;

    public Utils getUtils() {
        return utils;
    }

    public void reset () {
        super.reset();

        utils = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();
        utils = new Utils();
    }

    @After
    public void cleanup () {
        deleteFile(TEST_FILE_NAME);
        deleteFile(TEST_TRUSTSTORE);
    }

    public String load (String filename) {
        byte[] data = null;
        FileInputStream fileInputStream = null;

        try {
            File file = new File(filename);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(filename);
            int space = (int) file.length();
            data = new byte[space];
            fileInputStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return Utils.bytesToString(data);
    }

    public static final String FILE_NAME = "serverkeystore";


    public void setupKeyStore (String filename) {
        String s = load(FILE_NAME);

    }

    public static final String TEST_FILE_NAME = "testfile";
    public static final String TEST_FILE_CONTENTS = "FEEDFEED000000020000000200000002000263610000015A4D29B1EB0005582E353039000003643082036030820248A003020102020900B9D2AA076007077B300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303231373137323835385A170D3138303231373137323835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100C1403BF5214A0602C7390A651C515C25963459AA7BD20EA64F9BC25268C9DE404194E03EABBACB268BF490BA72C44C08E19F153F489BD445681F72F4F9D97C6F4E65F5D4D677CE783A49F7DD49C2D19B250F619E8C3A0B5A6F8ECAF149915259728821DDE5D624DC57B8E82DBCB490B64F6B98B97C2F34D304F3607C75DB6FE9970B2D3CA4093BDB35F1FC7DECEEF5AC76E780CF37EA24E5265C26D3661B72427F45EA2358C1DF7FAA00A2103719EA7ABC1FD76FF0DAF0DBDFC23C05C04FC0CF6D9D235815149C2D8C44758C24B511BA1F0DC6461A8724D699DB17C9389BF234FBDE6AC344BF3279ED0DC00C7BD6933A36126E46C8DD438AB50C5BBDD0E4C3E30203010001A3533051301D0603551D0E041604145E8A55312C059F0C3037878F429B864C5D9EBED9301F0603551D230418301680145E8A55312C059F0C3037878F429B864C5D9EBED9300F0603551D130101FF040530030101FF300D06092A864886F70D01010B050003820101000D9E54163305E2234DDFAAE7A95FDF25B2BBDFEA90A80A92551F42D2EE8CF18071C62EB276A42094CA5E7D6C3FA823F00B729152ECF1DCF1068724D68F584BFCB280A890E698C35A89587D5455F9705B5BE5BFDABFFA4FFC323018920882592BEA7FEC9926E8F9979D47CE99F64506F43349FD8ACE4A0424CF438EE30D33F25C9F798543591C32E89CDF909844AEAF3C8B6E07076DA79E18CA23702592F2B9FB4F05B282891A13C058C0F1AD4C570C0CC0676E59E8C012C97BA2BE1A0E85EA878260C202316FE083A15F361AF4D0823BDF0EC1205396C8443FD7F162B6B3D20A57A65BA4EAF4FFAA535ECB15C8EB6CE0DEA7128C421608AC150E65E10D82D2540000000100067365727665720000015A4D29FC5C00000502308204FE300E060A2B060104012A021101010500048204EA65F71C7A5EB60E7D6F9201EBCEA7FCFDECB29233B5EA581516F71448B14EFC277B1A8A90EE7965E8C683EEB0AB266316CDF90FA2D93C600CA150B9F453F589FC965E9613175DF0B4C0A9AC70EA3AEE377B18A9ABAE431456F71D4CB02F9E9FD2222B3DEC0F17BC1AAE1B049FF6993CF9E399EEAA2259023F67FBDCF61FAA69EEBC9236CBEC0655D8F1646327BD9735C27D09707A369C8EC4BC24A367FFE5DDF846857E478D41D72B18E1C6C68EDD51AB3FD9324EDBD30F4414418039550025D2E7392952AC4B7936C4780FBE6039065765D4063972926F9C8056A3ED910227A6E9149D6D6AD542DDAA23D013BCB8F89FB86E6AB750099D69C3B197C0D9DA8FEAB704DB4185FAF68047228B2040E02238E7534EB323939AFE2F9351641A2DD41E6CB7D7F84F50FB7D64790028FC6F8752C8A9631B1BB754EA31424840A21148A2C23E8A63505FB6A394A1C9BA248BE0F436BF684B0BB39CAB2685E3C4983A9BA63068D2AEE4A2B74DB9CB2C05196681DE5192966B13C93CB58FFA5AB6B75936F104E3B6410C048FEF93A878B8B0BD60024402312A5F21CE6851CE6CE69DC0DC4885AD7F53F0E7171ACC4157BE1D3060844F67E1EBEF7E52B09A42CCFCFD0E95497FA6708971508488B7727EFA437359FF4CF4B46F1F433B33F6E8E1AD552C826A808C55203EE3151DBEA09D5134D8ED33D900BA13309B1051BDA225E61F5B831A5AEF086488D8FC4C145B9964CA9FA2CCEFBA9FFEC03F960DC23DB22B40C335F510BC3569B0A3EB1A186912639D429F9144013F07DE611B94EF71B748DF745A3B0AE65C8CB122EBCA90656E6DC7B3B07123B997957569938FA8CFC328CC201A2784FADF0F76D23F5138EC4D61EBA2FCC83CA7F3806B88C851161345D0101AEF9FE3A2866084437D4885A8A4F48204C39B3C17C48E9A66D9595F445D1E3D1A4F74854B40F2A8CFA6903499FB5AEADE7209CC48716001F32618ABFDA404F22F4885753653C173AE527D1BA55B564C4079A61A66CAF2A5F2F926FBDF91FB52CA9BB9E6EC1B9DCFF796681FAD86C28041256C1F4527B8BE29FDA02F8CD92C5D403AC8B4A1376F8BB2EAF37D61EA694C12D465C043AA8844EACDA50991EB8163677555F83E7DD85C0BDE59ACA20B6D3A6F51F1A23666E539CF9A5BE55106F9C5D09A39A6775D07013249384D37AC6110C698F495ACDABB09BE62B641EAADE18BF15D7BC271E3AC4DB1A9DBA1906C3355869BA43B5ACC1C89EEC6D3819728854429756E9B1A9E368173E81E31429014DE31B4370CCA83DF24AE3B4E72A08B55E3D2EF0DAE01C877D93E38E02DB0E4377DE717926CEFC4D482E774580DA528A1BB310A8463A40F93294D1C9D59EA7652CB058D4E258E1CA3D1A9131A8A19506E52CD82CC52B595054CA299326E6C4F723319045C434C13360BD895DA0804B7B2546B3B10C2BA8397F83A733B6957D265E3831F88786F55EADBFFD5735D63F5D6965D0E59BC3C4FA16C79BB8C7128899B7E7BB232314F0ACB321FEBA5B0BD7FE77993B18712B58030DB5C3CB3B5E1DD01FEC64BBD8CD7B825C8AF3F8D7B7C027C3CC9F60B55C6AA4E6D221B1B0EB84EB5A4CE67725B6CD2766CECEAA048EA571ADA7B189DB7742D9DA79FE093375846B9CD3A07F7A65FF6373E63AE151FC6634D420741A9AF468A2426DE5677AF3FDB2A6DBC9B1294B5CD92341FD2295B85F42E5DBD9AB59C3C7C1C0641791D2209CC882E02575952FE3B2C3F78D9093D2F68D797F4959B2D8B36DD8267439099C6000000020005582E353039000003313082032D30820215020900A57B24B7D63CE15D300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303231373137343033335A170D3138303231373137343033335A306C3110300E06035504061307556E6B6E6F776E3110300E06035504081307556E6B6E6F776E3110300E06035504071307556E6B6E6F776E3110300E060355040A1307556E6B6E6F776E3110300E060355040B1307556E6B6E6F776E3110300E06035504031307556E6B6E6F776E30820122300D06092A864886F70D01010105000382010F003082010A0282010100B182939004DC4123A6AE31C00BC9AED6EC093661C4C72C8AFBE41DF10803F555141F26BC0A663EF769D4EB176DB0BD91B9B774A59C5186AC745F95F0560FE9266515B21535653983A07E68E2E2E5416F43CA2486E4EFE8DD649AB5A17542C8A9B3B7C6E83F04D21F0AC5E1808784C80D6A63FEAADDD70969A1A8A5D79338C2DD3256F6BEF2D6D21F4E00CF7A05A46D4FF1BFD73489A559C6A0408DEB617AF2D8AF96BD76776FBEABDF461D275745AA58F1251C02276E86727066C9A2185C88A8870997AA7F1F4C63E7541BE18FEFE1B81F6627ED50D67DEB8390100305CF6767514139DC1F8872ED872188C4F3B9712D32B0D5D252496235C66D6C86E670FE650203010001300D06092A864886F70D01010B050003820101008A8305BD8C85A2CB7257BB5EC7C5AC0AF7117196C205EF5D9641E22409F23441BEAF4E2ED94E0066BE7B304124F8A2284B18DFD80A0D59ABF616AEEBC84BE08261EE86E2D9E3AE4C4C42475E8F4A80D414C4C5DE0151F26523B6B4BA4CC5303E7683804C196F29271547D48E05BF754A1F781618805985EAD293A937181733D903AAF9AC88FBFEEF41C0D14E98CCC030C8321D9B6D15F191145717078440134D1FE841D1A6769F081DCD9B3858E7CDE49E99CEA31D6DC72043043DB6AE8AF6BA6B62CF0EB447A411283E3D54421CD3B1826C841377977EA3C9A58EAAA8C3ADA7FA1237E4E33B9543D44B1CB44EC77E16E01D05C4140A048241D2B7B9D82E6FAC0005582E353039000003643082036030820248A003020102020900B9D2AA076007077B300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303231373137323835385A170D3138303231373137323835385A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100C1403BF5214A0602C7390A651C515C25963459AA7BD20EA64F9BC25268C9DE404194E03EABBACB268BF490BA72C44C08E19F153F489BD445681F72F4F9D97C6F4E65F5D4D677CE783A49F7DD49C2D19B250F619E8C3A0B5A6F8ECAF149915259728821DDE5D624DC57B8E82DBCB490B64F6B98B97C2F34D304F3607C75DB6FE9970B2D3CA4093BDB35F1FC7DECEEF5AC76E780CF37EA24E5265C26D3661B72427F45EA2358C1DF7FAA00A2103719EA7ABC1FD76FF0DAF0DBDFC23C05C04FC0CF6D9D235815149C2D8C44758C24B511BA1F0DC6461A8724D699DB17C9389BF234FBDE6AC344BF3279ED0DC00C7BD6933A36126E46C8DD438AB50C5BBDD0E4C3E30203010001A3533051301D0603551D0E041604145E8A55312C059F0C3037878F429B864C5D9EBED9301F0603551D230418301680145E8A55312C059F0C3037878F429B864C5D9EBED9300F0603551D130101FF040530030101FF300D06092A864886F70D01010B050003820101000D9E54163305E2234DDFAAE7A95FDF25B2BBDFEA90A80A92551F42D2EE8CF18071C62EB276A42094CA5E7D6C3FA823F00B729152ECF1DCF1068724D68F584BFCB280A890E698C35A89587D5455F9705B5BE5BFDABFFA4FFC323018920882592BEA7FEC9926E8F9979D47CE99F64506F43349FD8ACE4A0424CF438EE30D33F25C9F798543591C32E89CDF909844AEAF3C8B6E07076DA79E18CA23702592F2B9FB4F05B282891A13C058C0F1AD4C570C0CC0676E59E8C012C97BA2BE1A0E85EA878260C202316FE083A15F361AF4D0823BDF0EC1205396C8443FD7F162B6B3D20A57A65BA4EAF4FFAA535ECB15C8EB6CE0DEA7128C421608AC150E65E10D82D254DB479DBE9D0502221495C5977C87D501724545E8";
    public static final String TEST_ALIAS = "server";
    public static final String TEST_PASSWORD = "whatever";

    @Test
    public void testLoadKeySuccess () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);

        PrivateKey key = null;

        try {
            key = Utils.loadKey(TEST_FILE_NAME, TEST_PASSWORD, TEST_ALIAS);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        assert (null != key);
    }

    @Test
    public void testLoadKeyWrongPassword () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);

        PrivateKey key = null;

        try {
            key = Utils.loadKey(TEST_FILE_NAME, "wrong", TEST_ALIAS);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        assert (null == key);
    }

    @Test
    public void testLoadKeyNoFile () {
        PrivateKey key = null;

        try {
            key = Utils.loadKey(TEST_FILE_NAME, TEST_PASSWORD, TEST_ALIAS);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        assert (null == key);
    }

    @Test
    public void testLoadKeyWrongAlias () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);
        PrivateKey key = null;

        try {
            key = Utils.loadKey(TEST_FILE_NAME, TEST_PASSWORD, "wrong");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        assert (null == key);
    }

    public static final String TEST_TRUSTSTORE = "truststore";
    public static final String TEST_TRUSTSTORE_PASSWORD = "whatever";
    public static final String TEST_TRUSTSTORE_ALIAS = "ca";
    public static final String TEST_TRUSTSTORE_CONTENTS = "FEEDFEED000000020000000100000002000263610000015A82C644EF0005582E353039000003643082036030820248A003020102020900DAD88CCBA0E7DEF3300D06092A864886F70D01010B05003045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C7464301E170D3137303232383033333134305A170D3138303232383033333134305A3045310B30090603550406130241553113301106035504080C0A536F6D652D53746174653121301F060355040A0C18496E7465726E6574205769646769747320507479204C746430820122300D06092A864886F70D01010105000382010F003082010A0282010100BCB5081EB8EA94F33414C155C0FB5E7C68C705F42338FAB4A31B3D7F4DD90F13799A30E03AED0E1F89E6CC85A2D1A48CC3F2CD1216E29A2AD73216C84B4EBD782FCC052041E0E80B69EFCB501BB92E6B349AEDA835AF708BF7DE537565F122C1DCC3604F40FD7BE3E9906B3512F384A2C33180A0DE3C00E6EEBF9E4EF0A5C71ABAC085818D0409D4E8671B15959E1E10D29EF4080C5DFD937223FD3427EF42118E90A3B43FB7F24BFE06AEBF6A01DCE45606ED47433E8EAA267D0DD5197B2CE7B53CB18D97F0BEDD9991A61C9D90417FEB9BF0B10B741E574889BE108AE49CE84C19837671A453DB9F4D0A85E460C1C052C4403617B758F64C68DAE884A105390203010001A3533051301D0603551D0E04160414E5C1EF1606B42BC0EF79B408A7E970FE200DB722301F0603551D23041830168014E5C1EF1606B42BC0EF79B408A7E970FE200DB722300F0603551D130101FF040530030101FF300D06092A864886F70D01010B05000382010100AEC817DD1E9603DF8D851D462924BB219E9626C3C3528593168DDD65A4A4B6B47291466CAB18049D0F25276B3724B4103CED0BE1190C73E7CA51E4E2BAF38A1E410000B908D748C3AE549FC52D5FECEBD8116832B0D193E7F6264874800A2D12AF32202D33D752DB2F3522C2077DFCC58DAD3DE58974F1B95EA17AF0BFF9B6E049E10AEE48B8128D953E7F56F3F51448F319C611774F1CC003F6438766744EFC6DDBBE5EF9C6F6EA8E07BD523BC1EC1458CC49E5BB43AE9B86816F0AE720DC6C81AB26345E45356C5E6F161E938494E056E5B7267DA5624E8D4B6748C645522B88A1059C485C5B277A8B76C0DB880302FE45DCC7AA933676A07E2D79C2A4F7B4F881F8C4724EA128CDAAFA8E0FA90ED94E457073";

    @Test
    public void testLoadCertificateSuccess () {
        createFile(TEST_TRUSTSTORE, TEST_TRUSTSTORE_CONTENTS);

        X509Certificate certificate = null;

        try {
            certificate = Utils.loadCertificate(TEST_TRUSTSTORE, TEST_TRUSTSTORE_PASSWORD, TEST_TRUSTSTORE_ALIAS);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        assert (null != certificate);
    }

    @Test
    public void testLoadCertificateWrongPassword () {
        createFile(TEST_TRUSTSTORE, TEST_TRUSTSTORE_CONTENTS);

        X509Certificate certificate = null;

        try {
            certificate = Utils.loadCertificate(TEST_TRUSTSTORE, "wrong", TEST_TRUSTSTORE_ALIAS);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        assert (null == certificate);
    }

    @Test
    public void testLoadCertificateNoFile () {
        X509Certificate certificate = null;

        try {
            certificate = Utils.loadCertificate(TEST_TRUSTSTORE, TEST_TRUSTSTORE_PASSWORD, TEST_TRUSTSTORE_ALIAS);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        assert (null == certificate);
    }

    @Test
    public void testLoadKeyStoreSuccess () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);

        KeyStore keyStore = null;

        try {
            keyStore = Utils.loadKeyStore(TEST_FILE_NAME, TEST_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert (keyStore != null);
    }

    @Test
    public void testLoadKeyStoreWrongPassword () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);

        KeyStore keyStore = null;

        try {
            keyStore = Utils.loadKeyStore(TEST_FILE_NAME, "wrong");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert (keyStore == null);
    }

    @Test
    public void testKeyStoreNoFile () {
        KeyStore keyStore = null;

        try {
            keyStore = Utils.loadKeyStore(TEST_FILE_NAME, TEST_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert (keyStore == null);
    }

    @Test
    public void testCloseIgnoreExceptionsInputStream () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(TEST_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        try {
            assert (!fileInputStream.getFD().valid());
        } catch (IOException e) {
        }
    }

    @Test
    public void testCloseIgnoreExceptionsInputStreamException () {
        TestInputStream testInputStream = new TestInputStream();

        Utils.closeIgnoreExceptions(testInputStream);
    }

    @Test
    public void testCloseIgnoreExceptionsWriter () {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(TEST_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }

    @Test
    public void testCloseIgnoreExceptionsOutputStream () {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(TEST_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    @Test
    public void testCloseIgnoreExceptionsReader () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);

        FileReader fileReader = null;

        try {
            fileReader = new FileReader(TEST_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileReader);
        }
    }

    @Test
    public void testCloseLogExceptionsInputStream () {
        TestInputStream testInputStream = new TestInputStream();

        Utils.closeLogExceptions(testInputStream, getMockLogger());

        verify(getMockLogger(), atLeastOnce()).error(Matchers.anyString(), Matchers.any(IOException.class));
    }

    @Test
    public void testCloseLogExceptionsOutputStream () {
        TestOutputStream testOutputStream = new TestOutputStream();

        Utils.closeLogExceptions(testOutputStream, getMockLogger());

        verify(getMockLogger(), atLeastOnce()).error(Matchers.anyString(), Matchers.any(IOException.class));
    }

    public static final String TEST_SHA1 = "BA612E2B074B53812E7C621BC76ADFBA3718C0F9";

    @Test
    public void testCalculateSHA1String () {
        String sha1 = null;

        try {
            sha1 = Utils.calculateSha1(TEST_FILE_CONTENTS);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        assert(sha1.equals(TEST_SHA1));
    }

    public static final String BINARY_SHA1 = "FA0FA492DB5AB78E51DB9B0D6480662F648056B66AB5E39B29693AEC04D73328";

    @Test
    public void testCalculateSHA1FileInputStream () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);
        FileInputStream fileInputStream = null;
        String sha1 = null;

        try {
            fileInputStream = new FileInputStream(TEST_FILE_NAME);
            byte[] bytes = Utils.calculateSha1(fileInputStream);
            sha1 = Utils.bytesToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        assert (sha1.equals(BINARY_SHA1));
    }

    @Test
    public void testByteToHexString () {
        byte b = 1;
        String s = Utils.byteToHexString(b);
        assert (s.equals("01"));

        int i = b << 1;
        b = (byte) i;
        s = Utils.byteToHexString(b);
        assert (s.equals("02"));

        i = b << 1;
        b = (byte) i;
        s = Utils.byteToHexString(b);
        assert (s.equals("04"));

        i = b << 1;
        b = (byte) i;
        s = Utils.byteToHexString(b);
        assert (s.equals("08"));

        i = b << 1;
        b = (byte) i;
        s = Utils.byteToHexString(b);
        assert (s.equals("10"));

        i = b << 1;
        b = (byte) i;
        s = Utils.byteToHexString(b);
        assert (s.equals("20"));

        i = b << 1;
        b = (byte) i;
        s = Utils.byteToHexString(b);
        assert (s.equals("40"));

        i = b << 1;
        b = (byte) i;
        s = Utils.byteToHexString(b);
        assert (s.equals("80"));
    }

    public static final byte[] TEST_DATA = { 1, 2, 3, 4};
    public static final String TEST_HEX_STRING = "01020304";

    public static boolean equivalent (byte[] b1, byte[] b2)
    {
        if (b1.length != b2.length)
            return false;

        for (int i = 0; i < b1.length; i++)
        {
            if (b1[i] != b2[i])
                return false;
        }

        return true;
    }

    @Test
    public void testHexStringToBytes () throws IOException {
        byte[] buffer = Utils.hexStringToBytes(TEST_HEX_STRING);
        assert (equivalent(buffer, TEST_DATA));
    }

    @Test
    public void testToNibble () {
        int value = Utils.toNibble('0');
        assert (0 == value);

        value = Utils.toNibble('1');
        assert (1 == value);

        value = Utils.toNibble('A');
        assert (10 == value);

        value = Utils.toNibble('F');
        assert (15 == value);

        value = Utils.toNibble('B');
        assert (11 == value);
    }

    public static final String TEST_SHA1_2 = "9571A3EEEA934F95E451BB012EE5FCD3539CDD5B";

    @Test
    public void testCalculateSha1 () {
        try {
            String sha1 = Utils.calculateSha1(TEST_HEX_STRING);
            assert (TEST_SHA1_2.equals(sha1));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateTrustManagerFactorySuccess () {
        createFile(TEST_FILE_NAME, TEST_FILE_CONTENTS);

        TrustManagerFactory trustManagerFactory = null;
        try {
            trustManagerFactory = Utils.createTrustManagerFactory(TEST_FILE_NAME, TEST_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHexStringToString () throws IOException {
        String hexString = "01020304";
        byte[] data = hexString.getBytes();

        String hexStringBytes = Utils.bytesToString(data);
        String s = Utils.hexStringToString(hexStringBytes);

        assert (hexString.equals(s));
    }

    public static final String TEST_SHA1_3 = "D869DB7FE62FB07C25A0403ECAEA55031744B5FB";

    @Test
    public void testSha1LogExceptions () {
        String s = Utils.calculateSha1LogExceptions("whatever");
        assert (s.equals(TEST_SHA1_3));
    }
}
